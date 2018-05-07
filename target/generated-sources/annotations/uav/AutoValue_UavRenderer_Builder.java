

package uav;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UavRenderer_Builder extends UavRenderer.Builder {

  private final ImmutableSet<UavRenderer.Opts> vizOptions;

  AutoValue_UavRenderer_Builder(
      ImmutableSet<UavRenderer.Opts> vizOptions) {
    if (vizOptions == null) {
      throw new NullPointerException("Null vizOptions");
    }
    this.vizOptions = vizOptions;
  }

  @Override
  ImmutableSet<UavRenderer.Opts> vizOptions() {
    return vizOptions;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UavRenderer.Builder) {
      UavRenderer.Builder that = (UavRenderer.Builder) o;
      return (this.vizOptions.equals(that.vizOptions()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= vizOptions.hashCode();
    return h$;
  }

  private static final long serialVersionUID = 701437750634453331L;

}
