package com.bhagwat.scm.userService.constants;

public enum SubscriptionPlan {
    TRIAL(10), // 10 users for trial
    GOLD(1000), // 1000 users for gold
    DIAMOND(10000); // 10000 users for diamond

    private final int maxUsers;

    SubscriptionPlan(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public int getMaxUsers() {
        return maxUsers;
    }
}
