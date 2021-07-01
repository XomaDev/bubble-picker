package xyz.kumaraswamy.bubblepicker;

import android.Manifest;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.widget.FrameLayout;

import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.HVArrangement;
import com.google.appinventor.components.runtime.Label;
import com.google.appinventor.components.runtime.PermissionResultHandler;
import com.google.appinventor.components.runtime.ReplForm;
import com.google.appinventor.components.runtime.errors.YailRuntimeError;
import com.google.appinventor.components.runtime.util.YailList;
import xyz.kumaraswamy.bubblepicker.circularpicker.presenter.CircularPickerContract;
import xyz.kumaraswamy.bubblepicker.circularpicker.ui.view.CircularPickerView;


import java.io.File;
import java.util.HashMap;

public class BubblePicker extends AndroidNonvisibleComponent {

  private final String tag;
  private final boolean companion;

  private final Activity activity;
  private final HashMap<String, CircularPickerView> pickerViewHashMap;

  public BubblePicker(ComponentContainer container) {
    super(container.$form());

    tag = "Bubble Picker";
    activity = container.$context();
    pickerViewHashMap = new HashMap<>();
    companion = form instanceof ReplForm;
  }

  @SimpleFunction(description = "Creates the Bubble picker animation int the arrangement.")
  public void CreateAnimation(String id, final HVArrangement arrangement, final YailList colors) {
    final CircularPickerView pickerView = pickerViewHashMap.containsKey(id) ?
            pickerViewHashMap.get(id) :
            new CircularPickerView(activity);

    final Object[] colorObjects = colors.toArray();
    final int[] colorIntObjects = new int[colorObjects.length];

    pickerView.setValueChangedListener(new CircularPickerContract.Behavior.ValueChangedListener() {
      @Override
      public void onValueChanged(int value) {
        ValueChanged(id, value);
      }
    });

    int index = 0;
    for(Object colorObject: colorObjects) {
      if(colorObject instanceof String) {
        String hexColor = colorObject.toString();
        if(!hexColor.startsWith("#"))
          hexColor = "#" + hexColor;
        colorIntObjects[index] = Color.parseColor(hexColor);
      } else {
        try {
          colorIntObjects[index] = Integer.parseInt(colorObject.toString());
        } catch (NumberFormatException formatException) {
          throw new YailRuntimeError("The input color at index " + (index + 1) + " is not valid.", tag);
        }
      }
      index++;
    }

    if(colorIntObjects.length > 0) {
      pickerView.setColors(colorIntObjects);
    }

    final FrameLayout arrangementLayout = (FrameLayout) arrangement.getView();
    arrangementLayout.removeAllViews();

    arrangementLayout.addView(pickerView);
    pickerViewHashMap.put(id, pickerView);
  }

  @SimpleFunction(description = "Sets the text for the animation.")
  public void CreateLabel(final String id, final Object textview, String typeface) {
    final CircularPickerView pickerView = findBubblePicker(id);

    if(!(textview instanceof Label)) {
      pickerView.setCenteredText(textview.toString());
      return;
    }

    final Label textview1 = (Label) textview;

    final String text = textview1.Text();

    if(!text.isEmpty()) {
      pickerView.setCenteredText(text);
      pickerView.setCenteredTextColor(textview1.TextColor());
      pickerView.setCenteredTextSize(textview1.FontSize());

      if(typeface == null || typeface.isEmpty()) {
        return;
      }

      if(typeface.contains("/") && form.isDeniedPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
        final String finalTypeface = typeface;
        form.askPermission(Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionResultHandler() {
          @Override
          public void HandlePermissionResponse(String permission, boolean granted) {
            if (granted) {
              CreateLabel(id, textview, finalTypeface);
            } else {
              form.dispatchPermissionDeniedEvent(BubblePicker.this, "CreateLabel", permission);
            }
          }
        });
        return;
      }

      if(!typeface.contains("/")) {
        if(companion) {
          final String packageName = activity.getPackageName();
          final String platform = packageName.contains("makeroid")
                  ? "Makeroid"
                  : packageName.contains("Niotron")
                  ? "Niotron"
                  : packageName.contains("Appzard")
                  ? "Appzard"
                  : "AppInventor";

          typeface = Build.VERSION.SDK_INT > 28
                  ? "/storage/emulated/0/Android/data/" + packageName + "/files/assets/" + typeface
                  : "/storage/emulated/0/" + platform + "/assets/" + typeface;
          pickerView.setCenteredTypeFace(Typeface.createFromFile(new File(typeface)));
        } else {
          pickerView.setCenteredTypeFace(Typeface.createFromAsset(activity.getAssets(), typeface));
        }
      }
    }
  }

  @SimpleFunction(description = "Sets extra properties for the bubble picker")
  public void SetProperties(final String id, final int gradientAngle, final int maxValue, final int maxLapCount) {
    if(!(maxValue > 1)) {
      throw new YailRuntimeError("Max value should be above 1", tag);
    }

    CircularPickerView pickerView = findBubblePicker(id);
    pickerView.setGradientAngle(gradientAngle);
    pickerView.setMaxValue(maxValue);
    pickerView.setMaxLapCount(maxLapCount);
  }

  @SimpleEvent(description = "Event fired when any value of any picker is changed")
  public void ValueChanged(final String id, int value) {
    EventDispatcher.dispatchEvent(this, "ValueChanged", id, value);
  }

  private CircularPickerView findBubblePicker(String id) {
    if(pickerViewHashMap.containsKey(id)) {
      return pickerViewHashMap.get(id);
    } else {
      throw new YailRuntimeError("The bubble picker ID " + id + " not found.", tag);
    }
  }
}
