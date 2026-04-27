package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.Y_ref;

class TLongLongHashMap$1 implements Y_ref {
   private boolean first;
   // $FF: synthetic field
   final TLongLongHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TLongLongHashMap$1(TLongLongHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(long var1, long var3) {
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
