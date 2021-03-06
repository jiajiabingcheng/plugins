// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.androidintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import java.util.Map;

/** AndroidIntentPlugin */
@SuppressWarnings("unchecked")
public class AndroidIntentPlugin implements MethodCallHandler {
  private final Context context;

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel =
        new MethodChannel(registrar.messenger(), "plugins.flutter.io/android_intent");
    channel.setMethodCallHandler(new AndroidIntentPlugin(registrar.activity()));
  }

  private AndroidIntentPlugin(Activity activity) {
    this.context = activity;
  }

  private String convertAction(String action) {
    switch (action) {
      case "action_view":
        return Intent.ACTION_VIEW;
      case "action_voice":
        return Intent.ACTION_VOICE_COMMAND;
      case "settings":
        return Settings.ACTION_SETTINGS;
      default:
        return action;
    }
  }

  private Bundle convertArguments(Map<String, ?> arguments) {
    Bundle bundle = new Bundle();
    for (String key : arguments.keySet()) {
      Object value = arguments.get(key);
      if (value instanceof Integer) {
        bundle.putInt(key, (Integer) value);
      } else if (value instanceof String) {
        bundle.putString(key, (String) value);
      } else if (value instanceof Boolean) {
        bundle.putBoolean(key, (Boolean) value);
      } else if (value instanceof Double) {
        bundle.putDouble(key, (Double) value);
      } else if (value instanceof Long) {
        bundle.putLong(key, (Long) value);
      } else {
        throw new UnsupportedOperationException("Unsupported type " + value);
      }
    }
    return bundle;
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    String action = convertAction((String) call.argument("action"));
    // Build intent
    Intent intent = new Intent(action);
    if (call.argument("category") != null) {
      intent.addCategory((String) call.argument("category"));
    }
    if (call.argument("data") != null) {
      intent.setData(Uri.parse((String) call.argument("data")));
    }
    if (call.argument("arguments") != null) {
      intent.putExtras(convertArguments((Map) call.argument("arguments")));
    }
    Log.i("android_intent plugin", "Sending intent " + intent);
    context.startActivity(intent);
    result.success(null);
  }
}
