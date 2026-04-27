package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.v;

class TDoubleFloatHashMap$1 implements v {
   private boolean first;
   // $FF: synthetic field
   final TDoubleFloatHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TDoubleFloatHashMap$1(TDoubleFloatHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(double var1, float var3) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var3);
      return true;
   }
}
