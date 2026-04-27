package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.aq;

class TShortObjectHashMap$1 implements aq {
   // $FF: synthetic field
   final TShortObjectHashMap this$0;

   TShortObjectHashMap$1(TShortObjectHashMap var1) {
      this.this$0 = var1;
   }

   public boolean execute(short var1, Object var2) {
      this.this$0.put(var1, var2);
      return true;
   }
}
