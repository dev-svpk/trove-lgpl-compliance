package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TFloatObjectProcedure;

class TFloatObjectHashMap$1 implements TFloatObjectProcedure {
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
