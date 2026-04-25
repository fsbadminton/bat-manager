package com.fsb.auth;

public final class PermissionConstants {
    private PermissionConstants() {}

    public static final String ADMIN_ACCESS = "admin:access";

    public static final String REVIEW_READ_ANY = "review:read:any";
    public static final String REVIEW_READ_OWN = "review:read:own";
    public static final String REVIEW_CREATE_OWN = "review:create:own";
    public static final String REVIEW_UPDATE_OWN = "review:update:own";
    public static final String REVIEW_DELETE_OWN = "review:delete:own";
    public static final String REVIEW_DELETE_ANY = "review:delete:any";

    public static final String ORDER_CREATE_OWN = "order:create:own";
    public static final String ORDER_READ_OWN = "order:read:own";
    public static final String ORDER_UPDATE_OWN = "order:update:own";
    public static final String ORDER_READ_ANY = "order:read:any";
    public static final String ORDER_UPDATE_ANY = "order:update:any";

    public static final String REFUND_APPLY_OWN = "refund:apply:own";
    public static final String REFUND_READ_OWN = "refund:read:own";
    public static final String REFUND_APPROVE_ANY = "refund:approve:any";

    public static final String PRODUCT_UPDATE_ANY = "product:update:any";

    public static final String COUPON_READ_OWN = "coupon:read:own";
    public static final String COUPON_CLAIM_OWN = "coupon:claim:own";
}
