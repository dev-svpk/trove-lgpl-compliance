package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.ac;

class TObjectByteHashMap$1 implements ac {
   // $FF: synthetic field
   final TObjectByteHashMap this$0;

   TObjectByteHashMap$1(TObjectByteHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(Object var1, byte var2) {
      this.this$0.put(var1, var2);
      return true;
   }
}
