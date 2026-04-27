package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.q;

class TCharShortHashMap$TKeyView$1 implements q {
   private boolean first;
   // $FF: synthetic field
   final TCharShortHashMap$TKeyView this$1;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TCharShortHashMap$TKeyView$1(TCharShortHashMap$TKeyView var1, StringBuilder var2) {
      this.this$1 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(char var1) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      return true;
   }
}
