package committee.nova.mkb.keybinding;

import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.util.IntHashMap;
import net.minecraft.client.settings.KeyBinding;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

public class KeyBindingMap {
    private static final EnumMap<KeyModifier, IntHashMap<Collection<KeyBinding>>> map = new EnumMap<>(KeyModifier.class);

    static {
        for (KeyModifier modifier : KeyModifier.values()) map.put(modifier, new IntHashMap<>());
    }

    @Nullable
    public KeyBinding lookupActive(int keyCode) {
        final KeyModifier activeModifier = KeyModifier.getActiveModifier();
        if (!activeModifier.matches(keyCode)) {
            final KeyBinding binding = getBinding(keyCode, activeModifier);
            if (binding != null) return binding;
        }
        return getBinding(keyCode, KeyModifier.NONE);
    }

    @Nullable
    private KeyBinding getBinding(int keyCode, KeyModifier keyModifier) {
        final Collection<KeyBinding> bindings = map.get(keyModifier).lookup(keyCode);
        if (bindings != null) {
            for (final KeyBinding binding : bindings) {
                if (((IKeyBinding) binding).isActiveAndMatches(keyCode)) {
                    return binding;
                }
            }
        }
        return null;
    }

    public List<KeyBinding> lookupAll(int keyCode) {
        final List<KeyBinding> matchingBindings = new ArrayList<>();
        for (final IntHashMap<Collection<KeyBinding>> bindingsMap : map.values()) {
            final Collection<KeyBinding> bindings = bindingsMap.lookup(keyCode);
            if (bindings != null) {
                matchingBindings.addAll(bindings);
            }
        }
        return matchingBindings;
    }

    public void addKey(int keyCode, KeyBinding keyBinding) {
        final KeyModifier keyModifier = ((IKeyBinding) keyBinding).getKeyModifier();
        final IntHashMap<Collection<KeyBinding>> bindingsMap = map.get(keyModifier);
        Collection<KeyBinding> bindingsForKey = bindingsMap.lookup(keyCode);
        if (bindingsForKey == null) {
            bindingsForKey = new ArrayList<>();
            bindingsMap.addKey(keyCode, bindingsForKey);
        }
        bindingsForKey.add(keyBinding);
    }

    public void removeKey(KeyBinding keyBinding) {
        final KeyModifier keyModifier = ((IKeyBinding) keyBinding).getKeyModifier();
        final int keyCode = keyBinding.getKeyCode();
        final IntHashMap<Collection<KeyBinding>> bindingsMap = map.get(keyModifier);
        final Collection<KeyBinding> bindingsForKey = bindingsMap.lookup(keyCode);
        if (bindingsForKey != null) {
            bindingsForKey.remove(keyBinding);
            if (bindingsForKey.isEmpty()) bindingsMap.removeObject(keyCode);
        }
    }

    public void clearMap() {
        for (final IntHashMap<Collection<KeyBinding>> bindings : map.values()) bindings.clearMap();
    }
}
