package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.Z_ref;

class TLongObjectHashMap$1 implements Z_ref {
   // $FF: synthetic field
   final TLongObjectHashMap this$0;

   TLongObjectHashMap$1(TLongObjectHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(long var1, Object var3) {
      this.this$0.put(var1, var3);
      return true;
   }
}
