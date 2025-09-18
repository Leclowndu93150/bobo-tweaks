package com.leclowndu93150.bobo_tweaks.additional.mixin;

import com.leclowndu93150.bobo_tweaks.additional.exclusiveitems.config.ExclusiveItemsConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(BundleItem.class)
public class BundleItemMixin {

    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private static void limitExclusiveItemsToOne(ItemStack bundle, ItemStack itemToAdd, CallbackInfoReturnable<Integer> cir) {
        if (!ExclusiveItemsConfig.ENABLE_MODULE.get()) {
            return;
        }

        TagKey<Item> exclusiveTag = bobo_tweaks$getExclusiveTag(itemToAdd);
        if (exclusiveTag != null) {
            int existingCount = bobo_tweaks$countExclusiveItemsInBundle(bundle, exclusiveTag);
            
            if (existingCount >= 1) {
                cir.setReturnValue(0);
            } else {
                int maxToAdd = Math.min(itemToAdd.getCount(), 1);
                if (maxToAdd < itemToAdd.getCount()) {
                    cir.setReturnValue(maxToAdd);
                }
            }
        }
    }

    @Unique
    private static TagKey<Item> bobo_tweaks$getExclusiveTag(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return null;
        }

        for (String tagString : ExclusiveItemsConfig.EXCLUSIVE_TAGS.get()) {
            TagKey<Item> tag = bobo_tweaks$getItemTag(tagString);
            if (tag != null && itemStack.is(tag)) {
                return tag;
            }
        }
        return null;
    }

    @Unique
    private static int bobo_tweaks$countExclusiveItemsInBundle(ItemStack bundle, TagKey<Item> exclusiveTag) {
        return bobo_tweaks$getContents(bundle)
                .mapToInt(stack -> stack.is(exclusiveTag) ? stack.getCount() : 0)
                .sum();
    }

    @Unique
    private static Stream<ItemStack> bobo_tweaks$getContents(ItemStack bundle) {
        CompoundTag compoundtag = bundle.getTag();
        if (compoundtag == null) {
            return Stream.empty();
        } else {
            ListTag listtag = compoundtag.getList("Items", 10);
            return listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
        }
    }

    @Unique
    private static TagKey<Item> bobo_tweaks$getItemTag(String tagString) {
        try {
            ResourceLocation tagLocation = new ResourceLocation(tagString);
            return TagKey.create(Registries.ITEM, tagLocation);
        } catch (Exception e) {
            return null;
        }
    }
}