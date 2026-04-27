package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.Q_ref;

class TIntObjectHashMap$1 implements Q_ref {
   // $FF: synthetic field
   final TIntObjectHashMap this$0;

   TIntObjectHashMap$1(TIntObjectHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(int var1, Object var2) {
      this.this$0.put(var1, var2);
      return true;
   }
}
