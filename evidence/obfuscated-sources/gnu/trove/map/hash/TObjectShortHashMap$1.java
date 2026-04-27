package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.aj;

class TObjectShortHashMap$1 implements aj {
   // $FF: synthetic field
   final TObjectShortHashMap this$0;

   TObjectShortHashMap$1(TObjectShortHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(Object var1, short var2) {
      this.this$0.put(var1, var2);
      return true;
   }
}
