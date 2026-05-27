package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TShortObjectProcedure;

class TShortObjectHashMap$1 implements TShortObjectProcedure {
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
