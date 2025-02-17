package com.capacitorjs.plugins.keyboard;

import android.os.Handler;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "Keyboard")
public class KeyboardPlugin extends Plugin {

    private Keyboard implementation;

    @Override
    public void load() {
        execute(
            () -> {
                implementation = new Keyboard(getActivity());
                implementation.setKeyboardEventListener(this::onKeyboardEvent);
            }
        );
    }

    @PluginMethod
    public void show(final PluginCall call) {
        execute(
            () ->
                new Handler()
                    .postDelayed(
                        () -> {
                            implementation.show();
                            call.resolve();
                        },
                        350
                    )
        );
    }

    @PluginMethod
    public void hide(final PluginCall call) {
        execute(
            () -> {
                if (!implementation.hide()) {
                    call.reject("Can't close keyboard, not currently focused");
                } else {
                    call.resolve();
                }
            }
        );
    }

    @PluginMethod
    public void setAccessoryBarVisible(PluginCall call) {
        call.unimplemented();
    }

    @PluginMethod
    public void setStyle(PluginCall call) {
        call.unimplemented();
    }

    @PluginMethod
    public void setResizeMode(PluginCall call) {
        call.unimplemented();
    }

    @PluginMethod
    public void setScroll(PluginCall call) {
        call.unimplemented();
    }

    void onKeyboardEvent(String event, int size) {
        JSObject kbData = new JSObject();
        switch (event) {
            case Keyboard.EVENT_KB_WILL_SHOW:
            case Keyboard.EVENT_KB_DID_SHOW:
                // Convert Handler().postDelayed() 350ms to 0.35s
                String data = "{ 'keyboardHeight': " + size + ", 'keyboardAnimationDuration': " + 0.35 + " }";
                bridge.triggerWindowJSEvent(event, data);
                kbData.put("keyboardHeight", size);
                kbData.put("keyboardAnimationDuration", 0.35);
                notifyListeners(event, kbData);
                break;
            case Keyboard.EVENT_KB_WILL_HIDE:
            case Keyboard.EVENT_KB_DID_HIDE:
                bridge.triggerWindowJSEvent(event);
                notifyListeners(event, kbData);
                break;
        }
    }

    @Override
    protected void handleOnDestroy() {
        implementation.setKeyboardEventListener(null);
    }
}
