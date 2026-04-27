package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.p;

class TCharObjectHashMap$1 implements p {
   // $FF: synthetic field
   final TCharObjectHashMap this$0;

   TCharObjectHashMap$1(TCharObjectHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(char var1, Object var2) {
      this.this$0.put(var1, var2);
      return true;
   }
}
