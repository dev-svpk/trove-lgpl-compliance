package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.q;

class TShortCharHashMap$TValueView$1 implements q {
   private boolean first;
   // $FF: synthetic field
   final TShortCharHashMap$TValueView this$1;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TShortCharHashMap$TValueView$1(TShortCharHashMap$TValueView var1, StringBuilder var2) {
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
