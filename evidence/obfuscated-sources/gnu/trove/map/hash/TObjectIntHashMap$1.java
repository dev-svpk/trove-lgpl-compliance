package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.ag;

class TObjectIntHashMap$1 implements ag {
   // $FF: synthetic field
   final TObjectIntHashMap this$0;

   TObjectIntHashMap$1(TObjectIntHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(Object var1, int var2) {
      this.this$0.put(var1, var2);
      return true;
   }
}
