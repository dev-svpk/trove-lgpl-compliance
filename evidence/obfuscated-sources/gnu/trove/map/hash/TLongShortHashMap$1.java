package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.ab;

class TLongShortHashMap$1 implements ab {
   private boolean first;
   // $FF: synthetic field
   final TLongShortHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TLongShortHashMap$1(TLongShortHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(long var1, short var3) {
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
