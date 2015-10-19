(function($){
	var Verifier = function(settings){
		this.rules = $.extend(true, this.rules, settings.rules);
	};
	
	Verifier.prototype = {
		rules : {},
		
		defaultSettings : {
            inputs:[],
            rules:{},
            button:"",
            onButtonClick:$.noop,
            beforeSubmit:$.noop,
            checkAll:$.noop
        },	
		
		tipStatu : function(type,input,msg){
	    	input.data("tip").
	    		removeClass("focus_tip right_tip error_tip ajax_checking_tip").
	    		addClass(type+"_tip").
	    		html(msg?msg:"").
	    		css({
	        		"position" 	: "absolute",
	        		"top"		: input.position().top+Math.ceil((input.outerHeight()-28)/2),
	        		"left"		: input.position().left + input.innerWidth() + 17
	        	}).show();
	    },
	    
		/*正则匹配*/
		checkReg : function(rule, input){
			var setting = input.data("setting");
			if(rule.rule.test(input.val())){
				this.tipStatu("right", input, setting.rightMsg);
				return true;
			}else{
				this.tipStatu("error", input, rule.tip);
				return false;
			}
		},
		
		/*ajax checking*/
		ajaxCheck : function(setting,input){
			if(setting.ajax){
				var ajax = setting.ajax,result = false;
				$.ajax({
					url:ajax.url,
					type:"post",
					data:input.attr("name")+"="+input.val(),
					dataType:"text",
					timeout:5000,
					async:false,
					before:function(){tipStatu("ajax_check",input);},
					success:function(d){
						if(d == 1){
							tipStatu("right",input,ajax.successMsg);
							result = true;
						}else if(d == 0){
							tipStatu("error",input,ajax.errorMsg);
							result = false;
						}else{result = false;}
					},
					error:function(){
						tipStatu("error",input,"网络出错");
						result = false;
					}
				});
				return result;
			}else{return true;}
		},
		
		/*初始化（每个input的）验证环境*/
		initContext : function(form,  setting){
			var tip, input;
			$.each(setting.inputs, function(i, inputSetting){
				input = form.find('[name="'+ inputSetting.name +'"]');
				
				if(input.length != 0){
					tip = $("<span class='input_tip'></span>");
					input.data("tip", tip).data("setting", inputSetting);
					input.after(tip);
		    		if(setting.showTipsAfterInit && inputSetting.focusMsg){
		    			Verifier.prototype.tipStatu("focus", input, inputSetting.focusMsg);
		    		}
				}
	    	});
		},
		
		/*检查输入*/
		doValidate : function(input, form, rules){
			var setting = input.data("setting");
			var result 	= true;
			
			if(setting){
				$.each(setting.rules, function(i, ruleName){
					if(rules[ruleName] && result){
						var rule = rules[ruleName];
						if($.type(rule.rule) == "regexp"){
							result = Verifier.prototype.checkReg(rule, input) && result;
						} else if($.type(rule.rule) == "function"){
							var formData = {};
							
							$.each(form.serializeArray(),function(i, item){
								formData[item.name] = item.value;
							});
							
							result = rule.rule(input.val(), formData) && result;
							result ? Verifier.prototype.tipStatu("right", input, setting.rightMsg) : Verifier.prototype.tipStatu("error", input, rule.tip);
						}
					}
				});
			}
			
			return result;
		}
	};
	
	$.fn.validateForm = function(settings){
		var form 		= $(this);
		var verifier 	= new Verifier(settings);
        var sets 		= $.extend(true, verifier.defaultSettings, settings);
    	var rules		= settings.rules;
        /*将设置信息分离出来，方便以后遍历*/
    	verifier.initContext(form, sets);
    	
    	/*失焦提示*/
        form.delegate("input,textarea","blur",function(){
	        verifier.doValidate($(this), form, rules);
        });  
        
        /*聚焦提示*/
        form.delegate("input,textarea","focus",function(){
        	var input = $(this);
        	var setting = input.data("setting");
        	if(setting && setting.focusMsg){
        		verifier.tipStatu("focus",input,setting.focusMsg);
        	}
        });
        
        /*提交表单*/
        form.submit(function(e){
        	var result = true;
        	var form = $(e.target);
        	$.each(form.find("input,textarea"),function(i,n){
            	result = (verifier.doValidate($(n), form, rules) && result);
        	});
        	if(!result) e.preventDefault();
        	sets.beforeSubmit(e,result,form);
        });
        
        /*如果指定了一个响应事件的元素，则在该元素被点击时执行此方法*/
        if(sets.button){
        	form.delegate(sets.button,"click",function(e){
        		var result = true;
        		$.each(form.find("input,textarea"),function(i, n){
                	result = (verifier.doValidate($(n), form,  rules) && result);
        		});
        		/*第一个参数指按钮,第二个参数指表单,第三个参数指表单的验证结果：通过与否*/
        		sets.onButtonClick(result, $(this), form);
        	});
        }
       
        return this;
	};
})(jQuery);