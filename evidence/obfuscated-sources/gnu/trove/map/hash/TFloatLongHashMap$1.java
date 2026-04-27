package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.G_ref;

class TFloatLongHashMap$1 implements G_ref {
   private boolean first;
   // $FF: synthetic field
   final TFloatLongHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TFloatLongHashMap$1(TFloatLongHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(float var1, long var2) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var2);
      return true;
   }
}
