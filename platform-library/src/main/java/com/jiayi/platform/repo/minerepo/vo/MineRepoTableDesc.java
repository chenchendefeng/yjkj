package com.jiayi.platform.repo.minerepo.vo;

import java.util.List;

public class MineRepoTableDesc {
    private String name;
    private List<FieldDesc> fields;
    private List<OrderDesc> orderBy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FieldDesc> getFields() {
        return fields;
    }

    public void setFields(List<FieldDesc> fields) {
        this.fields = fields;
    }

    public List<OrderDesc> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(List<OrderDesc> orderBy) {
        this.orderBy = orderBy;
    }

    public static class FieldDesc {
        private String name;
        private String type;
        private String uiType;
        private String desc;
        private Boolean searchable;
        private Boolean isCollision;
        private String relatedObjType;
        private String searchSign;
        private Boolean isDeleteField;
        private Boolean isSourceField;
        private Boolean isPK;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Boolean getSearchable() {
            return searchable;
        }

        public void setSearchable(Boolean searchable) {
            this.searchable = searchable;
        }

        public Boolean getCollision() {
            return isCollision;
        }

        public void setCollision(Boolean collision) {
            isCollision = collision;
        }

        public String getSearchSign() {
            return searchSign;
        }

        public void setSearchSign(String searchSign) {
            this.searchSign = searchSign;
        }

        public Boolean getDeleteField() {
            return isDeleteField;
        }

        public void setDeleteField(Boolean deleteField) {
            isDeleteField = deleteField;
        }

        public Boolean getSourceField() {
            return isSourceField;
        }

        public void setSourceField(Boolean sourceField) {
            isSourceField = sourceField;
        }

        public Boolean getPK() {
            return isPK;
        }

        public void setPK(Boolean PK) {
            isPK = PK;
        }

        public String getUiType() {
            return uiType;
        }

        public void setUiType(String uiType) {
            this.uiType = uiType;
        }

        public String getRelatedObjType() {
            return relatedObjType;
        }

        public void setRelatedObjType(String relatedObjType) {
            this.relatedObjType = relatedObjType;
        }
    }

    public static class OrderDesc {
        private String name;
        private String order;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOrder() {
            return order;
        }

        public void setOrder(String order) {
            this.order = order;
        }
    }
}
