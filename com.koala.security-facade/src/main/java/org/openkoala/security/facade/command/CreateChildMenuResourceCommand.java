package org.openkoala.security.facade.command;

public class CreateChildMenuResourceCommand extends CreateMenuResourceCommand {

    private Long parentId;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

}
