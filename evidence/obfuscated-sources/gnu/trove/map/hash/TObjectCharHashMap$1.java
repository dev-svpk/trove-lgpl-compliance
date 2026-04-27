package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.ad;

class TObjectCharHashMap$1 implements ad {
   // $FF: synthetic field
   final TObjectCharHashMap this$0;

   TObjectCharHashMap$1(TObjectCharHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(Object var1, char var2) {
      this.this$0.put(var1, var2);
      return true;
   }
}
