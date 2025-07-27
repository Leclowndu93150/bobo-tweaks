package com.leclowndu93150.bobo_tweaks.util;

public interface PlayerJumpAccess {
    void boboTweaks$setJumpKeyPressed(boolean pressed);
    boolean boboTweaks$isJumpKeyPressed();
    int boboTweaks$getJumpsUsed();
    void boboTweaks$setJumpsUsed(int jumps);
    boolean boboTweaks$canMultiJump();
}