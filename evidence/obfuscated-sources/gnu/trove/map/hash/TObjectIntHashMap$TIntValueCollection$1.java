package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.R_ref;

class TObjectIntHashMap$TIntValueCollection$1 implements R_ref {
   private boolean first;
   // $FF: synthetic field
   final TObjectIntHashMap$TIntValueCollection this$1;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TObjectIntHashMap$TIntValueCollection$1(TObjectIntHashMap$TIntValueCollection var1, StringBuilder var2) {
      this.this$1 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(int var1) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      return true;
   }
}
