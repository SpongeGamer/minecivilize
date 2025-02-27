package net.sponge.minecivilize.item;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class MyEquipmentInfoProvider implements DataProvider {

    private final PackOutput.PathProvider path;

    public MyEquipmentInfoProvider(PackOutput output) {
        this.path = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
    }

    private void add(BiConsumer<ResourceLocation, Object> registrar) {
        registrar.accept(
            ResourceLocation.fromNamespaceAndPath("minecivilize", "bronze"),
            new Object() // Placeholder for EquipmentClientInfo
        );
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<ResourceLocation, Object> map = new HashMap<>();
        this.add((name, info) -> {
            if (map.putIfAbsent(name, info) != null) {
                throw new IllegalStateException("Tried to register equipment client info twice for id: " + name);
            }
        });
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "Equipment Client Infos: minecivilize";
    }
} 