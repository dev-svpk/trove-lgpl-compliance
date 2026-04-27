package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.H_ref;

class TFloatObjectHashMap$1 implements H_ref {
   // $FF: synthetic field
   final TFloatObjectHashMap this$0;

   TFloatObjectHashMap$1(TFloatObjectHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(float var1, Object var2) {
      this.this$0.put(var1, var2);
      return true;
   }
}
