/*
 * 表格组件
 */+ function() {"use strict";
	var Grid = function(element, options) {
		this.$element = $(element);
		this.options = options;
		this.pageSize = options.pageSize;
		this.pageNo = options.pageNo;
		this.showPage = options.showPage;
		this.searchCondition = {};
		if ( typeof options.searchCondition === 'object') {
			this.searchCondition = options.searchCondition;
		}
		this.sortName = null;
		this.sortOrder = null;
		if (options.sortName) {
			this.sortName = options.sortName;
		}
		if (options.sortOrder) {
			this.sortOrder = options.sortOrder;
		}
		this.itemsMap = {};
		this._initLayout();
		this._initButtons();
		this._initHead();
		this._initOptions();
		this._initEvents();
		if (this.options.autoLoad) {
			var self = this;
			setTimeout(function() {
				self._loadData();
			}, 0);
		}
	};
	Grid.DEFAULTS = {
		loadingText : '正在载入...', //数据载入时的提示文字
		noDataText : '没有数据', //没有数据时的提示文字
		isShowIndexCol : true, //是否显示索引列
		isShowButtons : true,
		autoLoad : true, //是否表格准备好时加载数据
		isShowPages : true, //是否显示分页
		isUserLocalData : false, //是否使用本地数据源
		method : 'POST', //请求数据方式
		identity : 'id', //主键
		lockWidth : false,
		pageSize : 10,
		pageNo : 0,
		showPage : 4
	};
	
	Grid.prototype = {
		Constructor : Grid,
		_initLayout : function() {
			this.table = $(Grid.DEFAULTS.TEMPLATE).appendTo(this.$element);
			this.buttons = this.$element.find('.buttons');
			this.searchContainer = this.$element.find('.search');
			this.condition = this.searchContainer.find('[data-role="condition"]');
			this.totalRecordHtml = this.$element.find('[data-role="total-record"]');
			this.startRecord = this.$element.find('[data-role="start-record"]');
			this.endRecord = this.$element.find('[data-role="end-record"]');
			this.pages = this.$element.find('.pages');
			this.grid = this.$element.find('.grid');
			this.gridBody = this.$element.find('.grid-body').css('width', this.$element.width());
			this.gridTableHead = this.$element.find('.grid-table-head').css('min-width', this.$element.width());
			this.gridTableHeadTable = this.gridTableHead.find('table');
			var width = this.$element.width();
			this.gridBody.css('width', width);
			this.gridTableBody = this.$element.find('.grid-table-body').css('width', width);
			this.gridTableBody.height(this.gridTableBody.height()*$(window).height()/620);
			this.gridTableBodyTable = this.gridTableBody.find('table');
			this.pageSizeSelect = this.$element.find('[data-role="pageSizeSelect"]');
			!this.options.isShowButtons && this.buttons.hide();
			!this.options.isShowPages && this.grid.find('tfoot').hide();
			this.colResizePointer = this.table.find('.colResizePointer');
			this.scale = $(window).width()/1280;
		},
		_initButtons : function() {
			var self = this;
			var buttons = self.options.buttons;
			if (buttons && buttons.length > 0) {
				for (var i = 0, j = buttons.length; i < j; i++) {
					var action = buttons[i].action;
					$(buttons[i].content).appendTo(self.buttons).on('click', {
						action : action
					}, function(e) {
						e.stopPropagation();
						e.preventDefault();
						self.$element.trigger(e.data.action, {
							data : self.selectedRowsIndex(),
							item : self.selectedRows()
						});
					});
				}
			} else {
				self.options.isShowButtons = false;
			}
		},
		_initHead : function() {
			var self = this;
			var columns = this.options.columns;
			if (!columns || columns.length == 0) {
				self.$element.message({
					type : 'warning',
					content : '没有列数据'
				});
			}
			var totalColumnWidth = 0;
			self.widthRgExp = /^[1-9]\d*\.?\d*(px){0,1}$/;
			var titleHtml = new Array();
			titleHtml.push('<tr>');
			if (this.options.isShowIndexCol) {
				titleHtml.push('<th width="50px;"><div class="checkerbox" data-role="selectAll"></div></th>');
			} else {
				titleHtml.push('<th width="50px;" style="display:none"><div class="checkerbox" data-role="selectAll"></div></th>');
			}
			for (var i = 0, j = columns.length; i < j; i++) {
				var column = columns[i];
				var width = column.width + '';
				titleHtml.push('<th index="' + i + '" width="');
				if (width.match(self.widthRgExp)) {
					width = width.replace('px', '');
					totalColumnWidth += parseInt(self.scale*parseInt(width));
					titleHtml.push(self.scale*parseInt(width) + 'px"');
				} else {
					titleHtml.push(column.width + '"');
				}
				if (column.sortable && column.sortName) {
					titleHtml.push(' class="sort" sortName="' + column.sortName + '" title="点击排序"');
				}
				titleHtml.push('>');
				titleHtml.push(column.title);
				if (!this.options.lockWidth) {
					titleHtml.push('<div class="colResize"></div>');
				}
				titleHtml.push('</th>');
			}
			this.gridTableHeadTable.html(titleHtml.join(''));
			if (totalColumnWidth > this.$element.width()) {
				this.gridTableHeadTable.css('width', totalColumnWidth);
				this.gridTableBodyTable.css('width', totalColumnWidth);
			} else {
				this.gridTableHead.css('width', this.$element.width());
				this.gridTableBodyTable.css('width', this.$element.width());
				this.gridTableHeadTable.find('th:last').attr('width', 'auto');
				this.options.columns[this.options.columns.length - 1].width = 'auto';
			}
			this.gridTableHeadTable.find('[data-role="selectAll"]').on('click', function(e) {
				e.stopPropagation();
				var $this = $(this);
				if ($this.hasClass('checked')) {
					self.gridTableBodyTable.find('[data-role="indexCheckbox"]').each(function() {
						if ($(this).hasClass('checked')) {
							$(this).removeClass('checked').closest('tr').removeClass('success');
							self.$element.trigger('selectedRow', {
								checked : false,
								item : self.items[$(this).attr('indexValue')]
							});
							;
						}
					});
				} else {
					self.gridTableBodyTable.find('[data-role="indexCheckbox"]').each(function() {
						if (!$(this).hasClass('checked')) {
							$(this).addClass('checked').closest('tr').addClass('success');
							self.$element.trigger('selectedRow', {
								checked : true,
								item : self.items[$(this).attr('indexValue')]
							});
							;
						}
					});
				}
				$this.toggleClass('checked');
			});
			var sorts = this.gridTableHeadTable.find('.sort');
			sorts.on('click', function(e) {
				e.stopPropagation();
				var $this = $(this);
				self.sortName = $this.attr('sortName');
				if ($this.hasClass('sorting-asc')) {
					sorts.removeClass('sorting-asc').removeClass('sorting-desc').find('span').remove();
					$this.removeClass('sorting-asc').addClass('sorting-desc');
					$this.find('span').remove().end().append($('<span class="glyphicon glyphicon-arrow-down"></span>'));
					self.sortOrder = 'desc';
				} else {
					sorts.removeClass('sorting-asc').removeClass('sorting-desc').find('span').remove();
					$this.removeClass('sorting-desc').addClass('sorting-asc');
					$this.find('span').remove().end().append($('<span class="glyphicon glyphicon-arrow-up"></span>'));
					self.sortOrder = 'asc';
				}
				self._loadData();
			});
			this.gridTableHeadTable.find('.colResize').on('mousedown', function(e) {
				e.stopPropagation();
				var $this = $(this);
				var start = e.pageX;
				var left = self.gridTableHead.offset().left;
				self.colResizePointer.css({
					'height' : self.gridBody.height(),
					'left' : e.pageX - self.gridTableBody.scrollLeft() - left
				}).show();
				self.grid.css({
					'-moz-user-select' : 'none',
					'cursor' : 'move'
				}).on({
					'selectstart' : function() {
						return false;
					},
					'mousemove' : function(e) {
						self.colResizePointer.css({
							'left' : e.pageX - self.gridTableBody.scrollLeft() - left
						}).show();
					},
					'mouseup' : function(e) {
						var end = e.pageX;
						var $th = $this.parent();
						var width = parseFloat($th.attr('width')) + end - start;
						$th.attr('width', width);
						var index = $th.attr('index');
						self.gridTableBodyTable.find('td[index="' + index + '"]').attr('width', width);
						$(this).css({
							'-moz-user-select' : '-moz-all',
							'cursor' : 'default'
						}).off('selectstart').off('mouseup').off('mousemove');
						self.colResizePointer.hide();
						self.options.columns[index].width = width;
					}
				});
			});
		},
		_initOptions : function() {
			var self = this;
			//每页记录数
			this.pageSizeSelect.select({
				contents : [{
					value : '5',
					title : '5'
				}, {
					value : '10',
					title : '10'
				}, {
					value : '20',
					title : '20'
				}, {
					value : '50',
					title : '50'
				}, {
					value : '100',
					title : '100'
				}]
			});
			this.pageSizeSelect.setValue(this.options.pageSize).on('change', function() {
				self.pageSize = $(this).getValue();
				self.pageNo = Grid.DEFAULTS.pageNo;
				self._loadData();
			});
			if (self.options.querys && self.options.querys.length > 0) {
				this.condition.select({
					title : '选择条件',
					contents : self.options.querys
				});
			} else {
				this.searchContainer.hide();
				!this.options.isShowButtons && this.searchContainer.parent().hide();
			}
		},
		_initEvents : function() {
			var self = this;
			this.gridTableBody.on('scroll', function() {
				self.gridTableHead.css('left', -$(this).scrollLeft());
			});
			this.searchContainer.find('button[data-role="searchBtn"]').on('click', function() {
				for (var i = 0, j = self.options.querys.length; i < j; i++) {
					delete self.searchCondition[self.options.querys[i].value];
				}
				var condition = self.condition.getValue();
				if (!condition) {
					self.$element.message({
						type : 'warning',
						content : '请选择查询条件'
					});
					return;
				}
				var value = self.searchContainer.find('input[data-role="searchValue"]').val().replace(/(^\s*)|(\s*$)/g, "");
				
				self.searchCondition[condition] = value;
				self.pageNo = Grid.DEFAULTS.pageNo;
				self._loadData();
			});
		},
		
		/*
		 *加载数据
		 */
		_loadData : function() {
			this.gridTableBody.loader();
			var self = this;
			var params = {};
			params.pagesize = self.pageSize;
			params.page = self.pageNo;
			for (var prop in self.searchCondition) {
				params[prop] = self.searchCondition[prop];
			}
			
			if (self.sortName && self.sortOrder) {
				params.sortname = self.sortName;
				params.sortorder = self.sortOrder;
			}
			if (self.options.isUserLocalData) {
				self.totalRecord = self.options.localData.length;
				var start = self.pageSize * self.pageNo;
				var end = self.pageSize * self.pageNo;
				if (!this.options.isShowPages) {
					start = 0;
					end = self.totalRecord;
				}
				self.startRecord.text(start + 1);
				self.endRecord.text(end + 1);
				self.totalRecordHtml.text(self.totalRecord);
				self.items = self.getItemsFromLocalData(start, end);
				self._initPageNo(self.totalRecord);
				if (!self.options.localData || self.options.localData.length == 0) {
					self.gridTableBodyTable.empty();
					self.gridTableBody.find('[data-role="noData"]').remove();
					self.gridTableBody.append($('<div data-role="noData" style="font-size:16px ; padding: 20px; width:' + self.gridTableBodyTable.width() + 'px;">' + self.options.noDataText + '</div>'));
					self.gridTableBody.loader('hide');
					self.$element.trigger('complate');
				} else {
					self.gridTableBody.find('[data-role="noData"]').remove();
					self.renderDatas();
				}
				return;
			}
			
			$.ajax({
				type : this.options.method,
				url : this.options.url,
				data : params,
				dataType : 'json',
				success : function(result){

					if (!result.data) {
						self.$element.message({
							type : 'error',
							content : '查询失败'
						});
						self.$element.trigger('complate');
						self.gridTableBody.loader('hide');
						return;
					}
					self.startRecord.text(result.start + 1);
					self.endRecord.text(result.start + result.pageSize);
					self.totalRecordHtml.text(result.resultCount);
					//self._initPageNo(result.Total)
					self.items = result.data;
					self.totalRecord = result.resultCount;
					if (result.data.length == 0) {
						self.pages.hide();
						self.gridTableBodyTable.empty();
						self.gridTableBody.find('[data-role="noData"]').remove();
						self.gridTableBody.append($('<div data-role="noData" style="font-size:16px ; padding: 20px; width:' + self.gridTableBodyTable.width() + 'px;">' + self.options.noDataText + '</div>'));
						self.gridTableBody.loader('hide');
						self.$element.trigger('complate');
					} else {
						self.pages.show();
						self.gridTableBody.find('[data-role="noData"]').remove();
						self.renderDatas();
					}
					self.$element.trigger('complateRenderData', result);
				},
				error : function(XMLHttpRequest, textStatus, errorThrown){
					self.$element.message({
						type : 'error',
						content : '查询失败'
					});
					return;
				}
			});
		},
		
		update : function(result){
			var self = this;
			if (!result.data) {
				self.$element.message({
					type : 'error',
					content : '查询失败'
				});
				self.$element.trigger('complate');
				self.gridTableBody.loader('hide');
				return;
			}
			self.startRecord.text(result.start + 1);
			self.endRecord.text(result.start + result.pageSize);
			self.totalRecordHtml.text(result.resultCount);
			//self._initPageNo(result.Total)
			self.items = result.data;
			self.totalRecord = result.resultCount;
			if (result.data.length == 0) {
				self.pages.hide();
				self.gridTableBodyTable.empty();
				self.gridTableBody.find('[data-role="noData"]').remove();
				self.gridTableBody.append($('<div data-role="noData" style="font-size:16px ; padding: 20px; width:' + self.gridTableBodyTable.width() + 'px;">' + self.options.noDataText + '</div>'));
				self.gridTableBody.loader('hide');
				self.$element.trigger('complate');
			} else {
				self.pages.show();
				self.gridTableBody.find('[data-role="noData"]').remove();
				self.renderDatas();
			}
			self.$element.trigger('complateRenderData', result);
		},
		/**
		 * 根据开始结束记录数从本地数据获取数据
		 */
		getItemsFromLocalData : function(start, end) {
			var items = [];
			if (end > (this.totalRecord - 1)) {
				end = this.totalRecord - 1;
			}
			for (var i = start; i <= end; i++) {
				items.push(this.options.localData[i]);
			}
			return items;
		},
		/*
		 * 初始化分页
		 */
		_initPageNo : function() {
			var self = this;
			var pageSize = self.pageSizeSelect.getValue();
			self.totalPage = Math.floor(self.totalRecord / pageSize);
			if (self.totalRecord % pageSize != 0) {
				self.totalPage++;
			}
			if (self.totalPage == 0) {
				self.pages.hide();
				return;
			}
			var pagination = self.pages.find('ul.pagination');
			var pageHtml = new Array();
			pageHtml.push('<li data-role="firstPage"><a href="#">&laquo;</a></li>');
			pageHtml.push('<li data-role="prev"><a href="#">&lsaquo;</a></li>');
			if (self.pageNo % self.showPage == 0) {
				self.pageNo != Grid.DEFAULTS.pageNo && pageHtml.push('<li><a href="#">...</a></li>');
				for (var i = self.pageNo; i < self.totalPage && i < (self.pageNo + self.showPage); i++) {
					pageHtml.push('<li data-value="' + i + '" data-role="pageNo"><a href="#">' + (i + 1) + '</a></li>');
				}
				(self.pageNo + self.showPage) < self.totalPage && pageHtml.push('<li><a href="#">...</a></li>');
			} else {
				var start = Math.floor(self.pageNo / self.showPage) * self.showPage;
				start != Grid.DEFAULTS.pageNo && pageHtml.push('<li><a href="#">...</a></li>');
				for (var i = start; i < self.totalPage && i < (start + self.showPage); i++) {
					pageHtml.push('<li data-value="' + i + '" data-role="pageNo"><a href="#">' + (i + 1) + '</a></li>');
				}
				(start + self.showPage) < self.totalPage && pageHtml.push('<li><a href="#">...</a></li>');
			}
			pageHtml.push('<li data-role="next"><a href="#">&rsaquo;</a></li>');
			pageHtml.push('<li data-role="lastPage" ><a href="#">&raquo;</a></li>');
			pagination.html(pageHtml.join('')).find('li[data-role="pageNo"]').on('click', function() {
				self.pageNo = $(this).data('value');
				self._loadData();
			}).end().find('li[data-value="' + self.pageNo + '"]').addClass('active');
			self.prevBtn = pagination.find('li[data-role="prev"]').on('click', function() {
				if ($(this).hasClass('disabled')) {
					return;
				}
				$(this).addClass('disabled');
				self.pageNo--;
				self.pageOperateStatus = 'prev';
				self._loadData();
			});
			self.nextBtn = pagination.find('li[data-role="next"]').on('click', function() {
				if ($(this).hasClass('disabled')) {
					return;
				}
				$(this).addClass('disabled');
				self.pageNo++;
				self.pageOperateStatus = 'next';
				self._loadData();
			});
			self.firstPageBtn = pagination.find('li[data-role="firstPage"]').on('click', function() {
				if ($(this).hasClass('disabled')) {
					return;
				}
				$(this).addClass('disabled');
				self.pageNo = Grid.DEFAULTS.pageNo;
				self._loadData();
			});
			self.lastPageBtn = pagination.find('li[data-role="lastPage"]').on('click', function() {
				if ($(this).hasClass('disabled')) {
					return;
				}
				$(this).addClass('disabled');
				self.pageNo = self.totalPage - 1;
				self._loadData();
			});
			self.pageNo == Grid.DEFAULTS.pageNo && self.prevBtn.addClass('disabled') && self.firstPageBtn.addClass('disabled');
			self.pageNo == (self.totalPage - 1) && self.nextBtn.addClass('disabled') && self.lastPageBtn.addClass('disabled');
		},
		/*
		 * 渲染数据
		 */
		renderDatas : function() {
			var self = this;
			self.renderRows();
			self.initSelectRowEvent();
			self.options.isShowPages && self._initPageNo();
			self.$element.find('[data-role="selectAll"]').removeClass('checked');
			if (self.pageNo != self.totalPage - 1) {
				self.nextBtn && self.nextBtn.removeClass('disabled');
				self.lastPageBtn && self.lastPageBtn.removeClass('disabled');
			}
			if (self.pageNo != 0) {
				self.prevBtn && self.prevBtn.removeClass('disabled');
				self.firstPageBtn && self.firstPageBtn.removeClass('disabled');
			}
			self.$element.trigger('complate');
			self.gridTableBody.loader('hide');
		},
		initSelectRowEvent : function() {
			var self = this;
			var selectAll = self.gridTableHeadTable.find('[data-role="selectAll"]');
			var indexCheckboxs = this.gridTableBodyTable.find('[data-role="indexCheckbox"]');
			indexCheckboxs.off('click').on('click', function(e) {
				e.stopPropagation();
				var $this = $(this);
				if ($this.hasClass('checked')) {
					$this.removeClass('checked').closest('tr').removeClass('success');
				} else {
					$this.addClass('checked').closest('tr').addClass('success');
				}
				if (self.selectedRowsIndex().length == indexCheckboxs.length) {
					selectAll.addClass('checked');
				} else {
					selectAll.removeClass('checked');
				}
				self.$element.trigger('selectedRow', {
					checked : $this.hasClass('checked'),
					item : self.items[$this.attr('indexValue')]
				});
			});
			this.gridTableBodyTable.find('tr').off('click').on('click', function() {
				var $this = $(this);
				if ($this.hasClass('success')) {
					$this.removeClass('success').find('[data-role="indexCheckbox"]').removeClass('checked');
				} else {
					$this.addClass('success').find('[data-role="indexCheckbox"]').addClass('checked');
				}
				self.$element.trigger('selectedRow', {
					checked : $this.hasClass('success'),
					item : self.items[$this.find('[data-role="indexCheckbox"]').attr('indexValue')]
				});
				if (self.selectedRowsIndex().length == indexCheckboxs.length) {
					selectAll.addClass('checked');
				} else {
					selectAll.removeClass('checked');
				}
			});
		},
		/**
		 * 渲染表格数据
		 */
		renderRows : function() {
			var self = this;
			if (self.options.tree && self.options.tree.column) {
				self.items = self.initTreeItems(new Array(), self.items);
			}
			var items = self.items;
			items = JSON.parse(JSON.stringify(items).replace(/</g, '&lt;').replace(/>/g, '&gt;'));
			var trHtmls = new Array();
			for (var i = 0, j = items.length; i < j; i++) {
				var item = items[i];

				self.itemsMap[item[self.options.identity]] = item;
				var trHtml = new Array();
				if (self.options.tree && self.options.tree.column) {
					trHtml.push('<tr data-level=' + item.level + ' data-children=' + self.getChildrenCount(0, item.children) + '>');
				} else {
					trHtml.push('<tr>');
				}
				if (this.options.isShowIndexCol) {
					trHtml.push('<td width="50px;"><div class="checkerbox" indexValue="' + i + '" data-role="indexCheckbox" data-value="' + item[this.options.identity] + '"></div></td>');
				} else {
					trHtml.push('<td width="50px;" style="display:none"><div class="checkerbox" indexValue="' + i + '" data-role="indexCheckbox" data-value="' + item[this.options.identity] + '"></div></td>');
				}
				for (var k = 0, h = this.options.columns.length; k < h; k++) {
					var column = this.options.columns[k];
					var width = column.width.toString();
					if (width.match(self.widthRgExp)) {
						width = width.replace('px', '');
						width = self.scale*parseInt(width) + 'px';
					}
					trHtml.push('<td index="' + k + '" width="' + width + '"');
					if (column.align) {
						trHtml.push(' align="' + column.align + '"');
					}
					trHtml.push('>');
					if (self.options.tree && self.options.tree.column && self.options.tree.column == column.name) {
						trHtml.push('<div class="grid-tree-space" style="padding-left:' + (parseInt(item.level)) * 25 + 'px;"><span data-role="grid-tree-icon" class="glyphicon glyphicon-folder-open open"></span></div>&nbsp;&nbsp;');
					}
					if (column.render) {
						trHtml.push(column.render(item, column.name, i, k));
					} else {
						trHtml.push(item[column.name]);
					}
					trHtml.push('</td>');
				}
				trHtml.push('</tr>');
				trHtmls.push(trHtml.join(''));
			}
			this.gridTableBodyTable.html(trHtmls.join(''));
			if (self.options.tree && self.options.tree.column) {
				self.gridTableBodyTable.find('[data-role="grid-tree-icon"]').on('click', function(e) {
					e.stopPropagation();
					e.preventDefault();
					var $this = $(this);
					var $tr = $this.closest('tr');
					var level = parseInt($tr.attr('data-level'));
					var next = $tr.next();
					while (next.length > 0) {
						if (level < parseInt(next.attr('data-level'))) {
							if ($this.hasClass('open')) {
								next.hide();
								next.find('[data-role="grid-tree-icon"]').removeClass('glyphicon-folder-open').addClass('glyphicon-folder-close');
							} else {
								next.show();
								next.find('[data-role="grid-tree-icon"]').addClass('glyphicon-folder-open').removeClass('glyphicon-folder-close');
							}
							next = next.next();
						} else {
							break;
						}
					}
					if ($this.hasClass('open')) {
						$this.removeClass('open').removeClass('glyphicon-folder-open').addClass('glyphicon-folder-close');
					} else {
						$this.addClass('open').addClass('glyphicon-folder-open').removeClass('glyphicon-folder-close');
					}
				});
			}
		},
		/**
		 * 树形表格获取子节点下的所有数量
		 */
		getChildrenCount : function(count, items) {
			var self = this;
			count += items.length;
			$.each(items, function() {
				if (this.children) {
					count = self.getChildrenCount(count, this.children);
				}
			});
			return count;
		},
		/**
		 * 初始化树形数据
		 */
		initTreeItems : function(newItems, items) {
			var self = this;
			for (var i = 0, j = items.length; i < j; i++) {
				var item = items[i];
				newItems.push(item);
				if (item.children) {
					newItems = self.initTreeItems(newItems, item.children);
				}
			}
			return newItems;
		},
		/*
		 *返回选择行数据的数组。
		 */
		selectedRows : function() {
			var self = this;
			var selectItems = new Array();
			this.gridTableBodyTable.find('.checked[data-role="indexCheckbox"]').each(function() {
				selectItems.push(self.items[$(this).attr('indexvalue')]);
			});
			return selectItems;
		},
		/*
		 *返回选择行的序号。
		 */
		selectedRowsNo : function() {
			var selectIndexs = new Array();
			this.gridTableBodyTable.find('.checked[data-role="indexCheckbox"]').each(function() {
				selectIndexs.push($(this).attr('indexvalue'));
			});
			return selectIndexs;
		},
		/*
		 *返回所有行数据。
		 */
		getAllItems : function() {
			return this.items;
		},
		/*
		 *返回选择行索引的数组。
		 */
		selectedRowsIndex : function() {
			var selectIndexs = new Array();
			this.gridTableBodyTable.find('.checked[data-role="indexCheckbox"]').each(function() {
				selectIndexs.push($(this).attr('data-value'));
			});
			return selectIndexs;
		},
		/**
		 * 新增一行记录
		 */
		insertRows : function(items) {
			var self = this;
			if (!self.items) {
				self.items = new Array();
			}
			if (items.length) {
				$.each(items, function() {
					self.items.push(this);
					self.itemsMap[this[self.options.identity], this];
				});
			} else {
				self.items.push(items);
				self.itemsMap[items[this.options.identity]] = items;
			}
			self.gridTableBodyTable.empty();
			self.gridTableBody.find('[data-role="noData"]').remove();
			self.renderDatas();
			return this.$element;
		},
		removeRows : function(indexs) {
			var self = this;
			if(indexs.length){
				$.each(indexs, function() {
					delete self.itemsMap[this];
				});
			}else{
				delete self.itemsMap[indexs];
			}
			self.items = [];
			for (var prop in self.itemsMap) {
				self.items.push(self.itemsMap[prop]);
			}
			self.gridTableBodyTable.empty();
			self.renderDatas();
		},
		updateRows : function(currentKeyId, item) {
			var self = this;
			var index = self.getIndexByIdentityValue(currentKeyId);
			self.items[index] = item;
			self.itemsMap[item[self.options.identity]] = item;
			self.gridTableBodyTable.empty();
			self.renderDatas();
		},
		getIndexByIdentityValue : function(value) {
			return this.gridTableBodyTable.find('[data-value="' + value + '"]').closest('tr').index();
		},
		getRowByIndex : function(index) {
			return this.gridTableBodyTable.find('tr').eq(index);
		},
		getItemByIndex : function(index) {
			return this.items[index];
		},
		/**
		 * 刷新表格
		 */
		refresh : function() {
			this.pageNo = Grid.DEFAULTS.pageNo;
			this.gridTableHeadTable.find('[data-role="selectAll"]').removeClass('checked');
			this._loadData();
		},
		/**
		 * 销毁表格
		 */
		destory : function() {
			this.$element.data('koala.grid', null);
			this.$element.empty();
		},
		/**
		 * 外部查询
		 */
		search : function(conditions) {
            for (var prop in conditions) {
                this.searchCondition[prop] = conditions[prop];
            }
			this.pageNo = Grid.DEFAULTS.pageNo;
			this._loadData();
		},
		/**
		 * 上移
		 *
		 */
		up : function(index) {
			var self = this;
			if (index == 0) {
				return;
			}
			var currentRow = self.getRowByIndex(index);
			var prevRow = currentRow.prev('tr');
			var prevItem = self.items[parseInt(index) - 1];
			var currentItem = self.items[index];
			if (self.options.tree && self.options.tree.column) {
				if (parseInt(currentItem.level) > parseInt(prevItem.level)) {
					return false;
				} else {
					var tempItem = currentRow.prevAll('[data-level=' + currentItem.level + ']:first');
					if (tempItem.length > 0) {
						var tempIndex = tempItem.index();
						var upLevel = currentRow.prevAll('[data-level=' + parseInt(currentItem.level - 1) + ']:first');
						if (upLevel.length > 0) {
							if (tempIndex < upLevel.index()) {
								return false;
							}
						}
						prevRow = tempItem;
					}
				}
			}
			var childrenCount = parseInt(currentRow.attr('data-children'));
			var tempCurrentRow = currentRow.next();
			currentRow.insertBefore(prevRow);
			if (childrenCount > 0) {
				for (var i = 0; i < childrenCount; i++) {
					prevRow = currentRow;
					currentRow = tempCurrentRow;
					tempCurrentRow = currentRow.next();
					currentRow.insertAfter(prevRow);
				}
			}
			self.items = new Array();
			self.gridTableBodyTable.find('tr').each(function() {
				var $this = $(this);
				var indexCheckbox = $this.find('[data-role="indexCheckbox"]');
				indexCheckbox.attr('indexvalue', $this.index());
				self.items.push(self.itemsMap[indexCheckbox.attr('data-value')]);
			});
			return true;
		},
		/**
		 * 下移
		 *
		 */
		down : function(index) {
			var self = this;
			if (index == self.items.length) {
				return;
			}
			var currentRow = self.getRowByIndex(index);
			var nextRow = currentRow.next('tr');
			var nextItem = self.items[parseInt(index) + 1];
			var currentItem = self.items[index];
			if (self.options.tree && self.options.tree.column) {
				if (parseInt(currentItem.level) > parseInt(nextItem.level)) {
					return false;
				} else {
					var tempItem = currentRow.nextAll('[data-level=' + currentItem.level + ']:first');
					if (tempItem.length > 0) {
						var tempIndex = tempItem.index();
						var upLevel = currentRow.nextAll('[data-level=' + parseInt(currentItem.level - 1) + ']:first');
						if (upLevel.length > 0) {
							if (tempIndex > upLevel.index()) {
								return false;
							}
						}
						nextRow = tempItem;
						var childrenCount = parseInt(tempItem.attr('data-children'));
						for (var i = 0; i < childrenCount; i++) {
							nextRow = nextRow.next();
						}
					}
				}
			}
			var childrenCount = parseInt(currentRow.attr('data-children'));
			var tempCurrentRow = currentRow.next();
			currentRow.insertAfter(nextRow);
			if (childrenCount > 0) {
				for (var i = 0; i < childrenCount; i++) {
					nextRow = currentRow;
					currentRow = tempCurrentRow;
					tempCurrentRow = currentRow.next();
					currentRow.insertAfter(nextRow);
				}
			}
			self.items = new Array();
			self.gridTableBodyTable.find('tr').each(function() {
				var $this = $(this);
				var indexCheckbox = $this.find('[data-role="indexCheckbox"]');
				indexCheckbox.attr('indexvalue', $this.index());
				self.items.push(self.itemsMap[indexCheckbox.attr('data-value')]);
			});
			return true;
		}
	};
	$.fn.getGrid = function() {
		return $(this).data('koala.grid');
	};
	Grid.DEFAULTS.TEMPLATE = '<div class="table-responsive"><table class="table table-responsive table-bordered grid"><thead><tr><th><div class="btn-group buttons"></div><div class="search"><div class="btn-group select " data-role="condition"></div><div class="input-group" style="width:180px;"><input type="text" class="input-medium form-control" placeholder="Search" data-role="searchValue"><div class="input-group-btn"><button type="button" class="btn btn-default" data-role="searchBtn"><span class="glyphicon glyphicon-search"></span></button></div></div></div></th></tr></thead><tbody><tr><td><div class="colResizePointer"></div><div class="grid-body"><div class="grid-table-head"><table class="table table-bordered"></table></div><div class="grid-table-body"><table class="table table-responsive table-bordered table-hover table-striped"></table></div></div></td></tr></tbody><tfoot><tr><td><div class="records">显示:<span data-role="start-record">1</span>-<span data-role="end-record">10</span>, 共<span data-role="total-record">0</span>条记录。&nbsp;每页显示:<div class="btn-group select " data-role="pageSizeSelect"></div>条</div><div><div class="btn-group pages"><ul class="pagination"></ul></div></div></td></tr></tfoot></table></div>';
	var old = $.fn.grid;
	$.fn.grid = function(option) {
		return this.each(function() {
			var $this = $(this);
			var data = $this.data('koala.grid');
			var options = $.extend({}, Grid.DEFAULTS, $this.data(), typeof option == 'object' && option);
			if (!data) {
				$this.data('koala.grid', ( data = new Grid(this, options)));
			}
			if ( typeof option == 'string') {
				data[option]();
			}
		});
	};
	
	
	
	$.fn.grid.Constructor = Grid;
	$.fn.grid.noConflict = function() {
		$.fn.grid = old;
		return this;
	};
}(window.jQuery)

/*
 消息提示组件
 */ + function($) {"use strict";
	var Message = function(container, options) {
		this.container = $(container);
		this.$element = $(Message.DEFAULTS.TEMPLATE);
		this.options = options;
		this.init();
	};
	Message.DEFAULTS = {
		delay : 2000,
		type : 'info'
	};
	Message.prototype.init = function() {
		var self = this;
		$('.message').remove();
		this.content = this.$element.find('[data-toggle="content"]').html(this.options.content);
		switch(this.options.type){
			case 'success':
				this.content.before($('<div style="float:left;"><span class="glyphicon glyphicon-ok-sign" style="margin-right: 5px; font-size:18px;"/></div>'));
				this.$element.addClass('alert-success');
				break;
			case 'info':
				this.content.before($('<div style="float:left;"><span class="glyphicon glyphicon-info-sign" style="margin-right: 5px;font-size:18px;"/></div>'));
				this.$element.addClass('alert-info');
				break;
			case 'warning':
				this.content.before($('<div style="float:left;"><span class="glyphicon glyphicon-warning-sign" style="margin-right: 5px;font-size:18px;"/></div>'));
				this.$element.addClass('alert-warning');
				break;
			case 'error':
				this.content.before($('<div style="float:left;"><span class="glyphicon glyphicon-remove-sign" style="margin-right: 5px;font-size:18px; "/></div>'));
				this.$element.addClass('alert-danger');
				break;
		}
		this.$element.appendTo($('body')).fadeIn(400, function() {
			var width = self.$element.find('[data-toggle="content"]').outerWidth(true) * 0.5 + 20;
			var height = self.$element.find('[data-toggle="content"]').outerHeight(true) * 0.5 + 20;
			var left = self.container.offset().left + self.container.outerWidth(true) * 0.5 - width;
			var top = self.container.offset().top + self.container.outerHeight(true) * 0.5 - height;

			self.$element.css({
				'position' : 'fixed',
				'left' : left + 'px',
				'top' : top + 'px'
			})
		});
		setTimeout(function() {
  			self.$element.fadeOut(1000, function() {
  				$(this).remove();
  			});
		}, this.options.delay);
	};
	Message.DEFAULTS.TEMPLATE = '<div class="alert message" style="min-width: 120px;max-width: 300px; padding: 8px;text-align: left;z-index: 20000;">' + '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>' + '<span data-toggle="content" style="font-size:14px; word-wrap:break-word;"></span>&nbsp;&nbsp;</div>';
	var old = $.fn.message;
	$.fn.message = function(option) {
		return this.each(function() {
			var $this = $(this);
			var data = $this.data('koala.message');
			var options = $.extend({}, Message.DEFAULTS, $this.data(), typeof option == 'object' && option);
			$this.data('koala.message', ( data = new Message(this, options)));
			if ( typeof option == 'string') {
				data[option]();
			}
		});
	};
	$.fn.message.Constructor = Message;
	$.fn.message.noConflict = function() {
		$.fn.message = old;
		return this;
	};
}(window.jQuery)

/**
 * 修改密码
 */ + function($) {"use strict";
	var ModifyPassword = function(container, option) {
		this.container = $(container);
		this.options = option;
		this.$element = $(ModifyPassword.DEFAULTS.TEMPLATE);
		this.$element.modal({
			keyboard : false
		}).on({
			'hidden.bs.modal' : function() {
				$(this).remove();
			}
		});
		this.oldPwd = this.$element.find('#oldUserPassword');
		this.newPwd = this.$element.find('#newPassword');
		this.confirmPwd = this.$element.find('#confirmPassword');
		var thiz = this;
		this.$element.find(".btn-updatepassword").on("click",function(){
			ModifyPassword.prototype.save(thiz)
		});
	};
	ModifyPassword.DEFAULTS = {

	};
	ModifyPassword.prototype.blur = function(obj) {
		if (obj.val().length != 0) {
			if (obj.attr('id') == 'confirmPassword') {
				obj.val() == this.newPwd.val() && obj.parent().removeClass('has-error').end().next('.help-block').hide();
			} else if (obj.attr('id') == 'newPassword') {
				this.confirmPwd.val().length == 0 && obj.parent().removeClass('has-error').end().next('.help-block').hide();
				obj.val() == this.confirmPwd.val() && this.confirmPwd.parent().removeClass('has-error').end().next('.help-block').hide();
			} else {
				obj.parent().removeClass('has-error').end().next('.help-block').hide();
			}
		}
	};
	
	ModifyPassword.prototype.save = function(thiz) {
		var self = thiz;
		if (!Validation.notNull(self.$element, thiz.oldPwd, thiz.oldPwd.val(), '原始密码不能为空')) {
			return false;
		}
		if (!Validation.checkByRegExp(self.$element, thiz.oldPwd, '^[0-9a-zA-Z]*$', thiz.oldPwd.val(), '只能输入字母及数字')) {
			return false;
		}
		if (thiz.oldPwd.val().length < 6 || thiz.oldPwd.val().length > 10) {
			showErrorMessage(self.$element, thiz.oldPwd, '请输入6-10位数字或者字母');
			return false;
		}
		if (!Validation.notNull(self.$element, thiz.newPwd, thiz.newPwd.val(), '新密码不能为空')) {
			return false;
		}
		if (!Validation.checkByRegExp(self.$element, thiz.newPwd, '^[0-9a-zA-Z]*$', thiz.newPwd.val(), '只能输入字母及数字')) {
			return false;
		}
		if (thiz.newPwd.val().length < 6 || thiz.newPwd.val().length > 10) {
			showErrorMessage(self.$element, thiz.newPwd, '请输入6-10位数字或者字母');
			return false;
		}
		if (!Validation.notNull(self.$element, thiz.confirmPwd, thiz.confirmPwd.val(), '确认密码不能为空')) {
			return false;
		}
		if (!Validation.checkByRegExp(self.$element, thiz.confirmPwd, '^[0-9a-zA-Z]*$', thiz.confirmPwd.val(), '只能输入字母及数字')) {
			return false;
		}
		if (thiz.confirmPwd.val().length < 6 || thiz.confirmPwd.val().length > 10) {
			showErrorMessage(self.$element, thiz.confirmPwd, '请输入6-10位数字或者字母');
			return false;
		}
		if (thiz.newPwd.val() != thiz.confirmPwd.val()) {
			self.$element.find('.modal-content').message({
				type : 'error',
				content : '新密码与确认密码不一致'
			});
			return;
		}
		var data = "oldUserPassword=" + thiz.oldPwd.val() + "&userPassword=" + thiz.newPwd.val();
		var url = contextPath + '/auth/currentUser/updatePassword.koala';
		$.ajax({
			type : "post",
			url : url,
			data : data,
			dataType:"json",
			success:function(msg){
				if (msg.success) {
					$('body').message({
						type : 'success',
						content : '修改成功'
					});
					self.$element.modal('hide');
					var logOut = contextPath+"/logout.koala";
					$.post(logOut,function(data){
						if(data.success){
							window.location.href = contextPath+"/login.koala";
						}
					});
				} else {
					self.$element.find('.modal-content').message({
						type : 'error',
						content : msg.errorMessage
					});
				}
			},
			error:function(){
				self.$element.find('.modal-content').message({
					type : 'error',
					content : '修改失败'
				});
			}
		});
	};
	/**
	 * 显示提示信息
	 */
	ModifyPassword.prototype.showErrorMessage = function($container, $element, content) {
		$element.popover({
			content : content,
			trigger : 'manual',
			container : $container
		}).popover('show').on({
			'blur' : function() {
				$element.popover('destroy');
				$element.parent().removeClass('has-error');
			},
			'keydown' : function() {
				$element.popover('destroy');
				$element.parent().removeClass('has-error');
			}
		}).focus().parent().addClass('has-error');
	};
	
	ModifyPassword.DEFAULTS.TEMPLATE = '<div class="modal fade" id="modifyPwd">' 
	+ '<div class="modal-dialog modify-pwd">' 
	+ '<div class="modal-content">' 
	+ '<div class="modal-header">' 
	+ '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' 
	+ '<h4 class="modal-title">更改密码</h4>'
	+ '</div>' + '<div class="modal-body"> ' 
	+ '<form class="form-horizontal" role="form">' 
	+ '<div class="form-group">' 
	+ '<label for="oldUserPassword" class="col-lg-3 control-label">原始密码:</label>' 
	+ '<div class="col-lg-9">'
	+ '<input type="password" class="form-control" style="width:80%;display:inline;" id="oldUserPassword" /><span class="required">*</span>'
	+ '</div> ' + '</div>  ' + '<div class="form-group">' 
	+ '<label for="newPassword" class="col-lg-3 control-label">新密码:</label>' 
	+ '<div class="col-lg-9">' 
	+ '<input type="password" class="form-control" style="width:80%;display:inline;" id="newPassword"/><span class="required">*</span>' 
	+ '</div> ' + '</div> ' + '<div class="form-group"> ' 
	+ '<label for="confirmPassword" class="col-lg-3 control-label">确认密码:</label>' 
	+ '<div class="col-lg-9">' 
	+ '<input type="password" class="form-control" style="width:80%;display:inline;" id="confirmPassword"/><span class="required">*</span> ' 
	+ '</div>' + '</div>' + '</form>' + '</div>' + '<div class="modal-footer"> ' 
	+ '<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>' + '<button type="button" class="btn btn-success btn-updatepassword" data-toggle="save">保存</button>' + '</div>' + '</div>  ' + '</div>  ' + '</div>';
	var old = $.fn.modifyPassword;
	$.fn.modifyPassword = function(option) {
		
		return this.each(function() {
			var $this = $(this);
			var data = $this.data('koala.modifyPassowrd');
			var options = $.extend({}, ModifyPassword.DEFAULTS, $this.data(), typeof option == 'object' && option)
			$this.data('koala.modifyPassowrd', ( data = new ModifyPassword(this, options)));
		});
	};
	$.fn.modifyPassword.Constructor = ModifyPassword;
	$.fn.modifyPassword.noConflict = function() {
		$.fn.modifyPassword = old;
		return this;
	};
}(window.jQuery)

/*
 *选择框组件
 */ + function($) {"use strict" ;
	var Select = function(element, options) {
		this.$element = $(element);
		this.options = options;
		this.$element.html(Select.DEFAULTS.TEMPLATE);
		this.$button = this.$element.find('[data-toggle="button"]');
		this.$items = this.$element.find('.dropdown-menu');
		this.$item = this.$element.find('[data-toggle="item"]');
		this.$value = this.$element.find('[data-toggle="value"]');
		this.init();
	};
	Select.DEFAULTS = {

	};
	Select.prototype = {
		Constructor : Select,
		init : function() {
			var self = this;
			this.$button.on('click', function() {
				self.$element.toggleClass('open');
			});
			this.$item.on('click', function() {
				self.$element.toggleClass('open');
			});
			this.$item.html(self.options.title);
			var contents = self.options.contents;
			if (contents && contents.length) {
				self.setItems(contents);
			}
			this.setDefaultSelection();
			self.$items.on('mouseleave', function() {
				self.$element.removeClass('open');
			});
		},
		setItems : function(contents) {
			var self = this;
			var items = new Array();
			for (var i = 0, j = contents.length; i < j; i++) {
				var content = contents[i];
				items.push('<li data-value="' + content.value + '"' + (content.selected && 'class="selected"') + '><a href="#">' + content.title + '</a></li>');
			}
			self.$items.html(items.join(' '));
			if (self.$items.find('li').length > 5) {
				self.$items.css({
					'height' : '130px',
					'overflow-y' : 'auto'
				});
			}
			self.$items.find('li').on('click', function(e) {
				e.preventDefault();
				self.clickItem($(this));
			});
			return self.$element;
		},
		clickItem : function($item) {
			this.$element.removeClass('open')
			this.$button.removeClass('active');
			var value = $item.data('value');
			this.$item.html($item.find('a:first').html());
			this.$value.val(value);
			this.$element.trigger('change').popover('destroy').parent().removeClass('has-error');
			return this.$element;
		},
		getValue : function() {
			return this.$value.val();
		},
		getItem : function() {
			return this.$item.html();
		},
		selectByValue : function(value) {
			return this.selectBySelector('li[data-value="' + value + '"]');
		},
		selectByIndex : function(index) {
			return this.selectBySelector('li:eq(' + index + ')');
		},
		selectBySelector : function(selector) {
			return this.clickItem(this.$items.find(selector));
		},
		setDefaultSelection : function() {
			return this.selectBySelector('li.selected');
		}
	};
	Select.DEFAULTS.TEMPLATE = '<button type="button" class="btn btn-default" data-toggle="item">' + '</button><button type="button" class="btn btn-default dropdown-toggle" data-toggle="button">' + '<span class="caret"></span>' + '</button>' + '<input type="hidden" data-toggle="value"/>' + '<ul class="dropdown-menu" role="menu"></ul>';
	$.fn.getValue = function() {
		if ($(this).data('koala.select')) {
			return $(this).data('koala.select').getValue();
		}
	};
	$.fn.getItem = function() {
		if ($(this).data('koala.select')) {
			return $(this).data('koala.select').getItem();
		}
	};
	$.fn.setValue = function(value) {
		if ($(this).data('koala.select')) {
			return $(this).data('koala.select').selectByValue(value);
		}
	};
	$.fn.setSelectItems = function(contents) {
		if ($(this).data('koala.select')) {
			return $(this).data('koala.select').setItems(contents);
		}
	};
	$.fn.appendItems = function(contents) {
		return $(this).data('koala.select').setItems(contents);
	};
	$.fn.resetItems = function(contents) {
		$(this).data('koala.select').$item.empty();
		return $(this).data('koala.select').setItems(contents);
	};
	var old = $.fn.select;
	$.fn.select = function(option) {
		return this.each(function() {
			var $this = $(this);
			//var data = $this.data('koala.select');
			var data;
			var options = $.extend({}, Select.DEFAULTS, $this.data(), typeof option == 'object' && option);
			//if (!data) {
				$this.data('koala.select', ( data = new Select(this, options)));
			//}
			if ( typeof option == 'string') {
				data[option]();
			}
		});
	};
	$.fn.select.Constructor = Select;
	$.fn.select.noConflict = function() {
		$.fn.select = old;
		return this;
	};
}(window.jQuery)

/*
 *消息确认组件
 */ + function($) {"use strict";
	var Confirm = function(container, options) {
		this.container = $(container);
		this.$element = $(Confirm.DEFAULTS.TEMPLATE);
		this.options = options;
		this.init();
	};
	Confirm.DEFAULTS = {
		backdrop : true
	};
	Confirm.prototype.init = function() {
		var self = this;
		this.$element.modal({
			backdrop : self.options.backdrop,
			keyboard : false
		}).find('.modal-dialog').css({
			'padding-top' : '120px'
		}).find('[data-role="confirm-content"]').html(this.options.content);
		this.$element.find('[data-role="confirmBtn"]').on('click', function() {
			if ( typeof self.options.callBack == 'function') {
				self.options.callBack();
				self.$element.modal('hide');
			}
		});
	};
	Confirm.DEFAULTS.TEMPLATE = '<div class="modal fade"  aria-hidden="false"><div class="modal-dialog" style=" width: 400px;"><div class="modal-content"><div class="modal-header"><button aria-hidden="true" data-dismiss="modal" class="close" type="button">×</button><h4 class="modal-title">确认</h4></div><div class="modal-body" style=" padding: 10px 40px;font-size: 16px;color: #c09853;"><span class="glyphicon glyphicon-warning-sign"></span>&nbsp;&nbsp;&nbsp;<span data-role="confirm-content">确定要删除吗?</span></div><div class="modal-footer"><button data-dismiss="modal" class="btn btn-default" type="button">取消</button><button data-role="confirmBtn" class="btn btn-danger" type="button">确定</button></div></div></div></div>';
	var old = $.fn.confirm;
	$.fn.confirm = function(option) {
		return this.each(function() {
			var $this = $(this);
			var data = $this.data('koala.confirm');
			var options = $.extend({}, Confirm.DEFAULTS, $this.data(), typeof option == 'object' && option);
			$this.data('koala.confirm', ( data = new Confirm(this, options)));
			if ( typeof option == 'string') {
				data[option]();
			}
		});
	};
	$.fn.confirm.Constructor = Confirm;
	$.fn.confirm.noConflict = function() {
		$.fn.confirm = old;
		return this;
	};
}(window.jQuery)

/*
 * 加载
 */ + function($) {"use strict";

	var Loader = function(element, options) {
		this.$element = $(element);
		this.options = options;
		this.init();
	};

	Loader.DEFAULTS = {
		title : '正在加载...',
		opacity: 0.1,
		imageData: 'data:image/gif;base64,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'
	};

	Loader.prototype.init = function() {
		var self = this;
		self.$element.css('position', 'relative');
		self.backdrop = $('<div class="modal-backdrop fade in" style="opacity:'+self.options.opacity+';filter:alpha(opacity='+self.options.opacity*100+');"></div>');
		self.backdrop.css({
			position : 'absolute',
			width : self.$element.outerWidth(),
			height : self.$element.outerHeight()
		});
		self.progress = $('<div style="width: 200px; z-index: 20000; position: absolute; text-align:center;"><div class="progress progress-striped active" style="margin-bottom:0;"><div class="progress-bar" style="width: 100%;"></div></div><h5>' + self.options.title + '</h5></div>');
		//兼容IE8 IE9
		if (window.ActiveXObject && parseInt(navigator.userAgent.toLowerCase().match(/msie ([\d.]+)/)[1]) < 10) {
			self.progress = $('<div style="width: 145px; z-index: 20000; position: absolute; text-align:center;"><img alt="正在加载" src="'+self.options.imageData+'" style="border-radius: 5px;width: 145px;height: 18px;"></img><h5>' + self.options.title + '</h5></div>');
		}
		self.progress.css({
			left : (self.$element.outerWidth() - self.progress.width()) / 2,
			top : (self.$element.outerHeight() - self.progress.height()) / 2
		});
		self.show();
	}

	Loader.prototype.show = function() {
		var self = this;
		self.$element.append(self.backdrop).append(self.progress);
	}

	Loader.prototype.hide = function() {
		var self = this;
		clearInterval(self.intervalId);
		self.backdrop.remove();
		self.progress.remove();
		self.$element.data('koala.loader', null);
	}
	var old = $.fn.loader;
	$.fn.loader = function(option) {
		return this.each(function() {
			var $this = $(this);
			var data = $this.data('koala.loader');
			var options = $.extend({}, Loader.DEFAULTS, $this.data(), typeof option == 'object' && option);
			if (!data) {
				$this.data('koala.loader', ( data = new Loader(this, options)));
			}
			if ( typeof option == 'string') {
				data[option]();
			}
		});
	};
	$.fn.loader.Constructor = Loader;
	$.fn.loader.noConflict = function() {
		$.fn.loader = old;
		return this;
	};
}(window.jQuery)

