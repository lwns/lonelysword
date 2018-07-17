package com.timper.lonelysword.compiler.unbinder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

/**
 * User: tangpeng.yang
 * Date: 21/05/2018
 * Description:
 * FIXME
 */
public class NetworkBinding implements CodeBinding {

  static final ClassName BROADCASTRECEIVER = ClassName.get("android.content", "BroadcastReceiver");
  static final ClassName CONTEXT = ClassName.get("android.content", "Context");
  static final ClassName INTENT = ClassName.get("android.content", "Intent");
  static final ClassName CONNECTIVITYMANAGER = ClassName.get("android.net", "ConnectivityManager");
  static final ClassName INTENTFILTER = ClassName.get("android.content", "IntentFilter");

  private final DefaultMethodBinding disableNetworkBinding;
  private final DefaultMethodBinding enableNetworkBinding;
  private final boolean isActivity;
  private final boolean isFragment;

  String target;

  public NetworkBinding(DefaultMethodBinding disableNetworkBinding, DefaultMethodBinding enableNetworkBinding, boolean isActivity,
      boolean isFragment) {
    this.disableNetworkBinding = disableNetworkBinding;
    this.enableNetworkBinding = enableNetworkBinding;
    this.isActivity = isActivity;
    this.isFragment = isFragment;
    target = isActivity ? "target" : "target.getActivity()";
  }

  @Override public CodeBlock render() {
    StringBuilder builder = new StringBuilder("new $1T() {\n"
        + "@Override public void onReceive(final $2T context, $3T intent) {\n"
        + "if ($4T.CONNECTIVITY_ACTION.equals(intent.getAction())) {\n"
        + "boolean isDisconnected = intent.getBooleanExtra($4T.EXTRA_NO_CONNECTIVITY, false);\n"
        + "if (isDisconnected) {\n");
    if (disableNetworkBinding != null) {
      builder.append(disableNetworkBinding.render().toString() + "\n");
    }
    builder.append("} else {\n");
    if (enableNetworkBinding != null) {
      builder.append(enableNetworkBinding.render().toString() + "\n");
    }
    builder.append("}\n" + "}\n" + "}\n" + "}");
    return CodeBlock.of(builder.toString(), BROADCASTRECEIVER, CONTEXT, INTENT, CONNECTIVITYMANAGER);
  }

  public CodeBlock bindNetworkConnection() {
    String target = isActivity ? "target" : "target.getActivity()";
    StringBuilder builder = new StringBuilder("$1T connectFilter = new $1T();\n"
        + "connectFilter.addAction($2T.CONNECTIVITY_ACTION);\n"
        + target
        + ".registerReceiver(connectReceiver, connectFilter);\n");
    return CodeBlock.of(builder.toString(), INTENTFILTER, CONNECTIVITYMANAGER);
  }

  public CodeBlock unBindNetworkConnection() {
    return CodeBlock.of(target + ".unregisterReceiver(connectReceiver);\n");
  }

  static final class Builder {
    private DefaultMethodBinding disableNetworkBinding;
    private DefaultMethodBinding enableNetworkBinding;
    private final boolean isActivity;
    private final boolean isFragment;

    Builder(boolean isActivity, boolean isFragment) {
      this.isActivity = isActivity;
      this.isFragment = isFragment;
    }

    void addDisableNetwor(DefaultMethodBinding disableNetworkBinding) {
      this.disableNetworkBinding = disableNetworkBinding;
    }

    void addenableNetwor(DefaultMethodBinding enableNetworkBinding) {
      this.enableNetworkBinding = enableNetworkBinding;
    }

    NetworkBinding build() {
      if (disableNetworkBinding == null && enableNetworkBinding == null) {
        return null;
      } else {
        return new NetworkBinding(disableNetworkBinding, enableNetworkBinding, isActivity, isFragment);
      }
    }
  }
}
