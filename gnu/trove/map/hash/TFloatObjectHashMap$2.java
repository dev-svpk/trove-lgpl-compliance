package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TFloatObjectProcedure;

class TFloatObjectHashMap$2 implements TFloatObjectProcedure {
   private boolean first;
   // $FF: synthetic field
   final TFloatObjectHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TFloatObjectHashMap$2(TFloatObjectHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(float var1, Object var2) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(",");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var2);
      return true;
   }
}
