package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.af;

class TObjectFloatHashMap$1 implements af {
   // $FF: synthetic field
   final TObjectFloatHashMap this$0;

   TObjectFloatHashMap$1(TObjectFloatHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(Object var1, float var2) {
      this.this$0.put(var1, var2);
      return true;
   }
}
