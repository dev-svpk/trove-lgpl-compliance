package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.ar;

class TObjectShortHashMap$TShortValueCollection$1 implements ar {
   private boolean first;
   // $FF: synthetic field
   final TObjectShortHashMap$TShortValueCollection this$1;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TObjectShortHashMap$TShortValueCollection$1(TObjectShortHashMap$TShortValueCollection var1, StringBuilder var2) {
      this.this$1 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(short var1) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      return true;
   }
}
