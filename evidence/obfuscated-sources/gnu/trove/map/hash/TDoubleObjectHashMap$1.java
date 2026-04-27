package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.y;

class TDoubleObjectHashMap$1 implements y {
   // $FF: synthetic field
   final TDoubleObjectHashMap this$0;

   TDoubleObjectHashMap$1(TDoubleObjectHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(double var1, Object var3) {
      this.this$0.put(var1, var3);
      return true;
   }
}
