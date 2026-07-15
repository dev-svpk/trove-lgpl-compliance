package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TDoubleObjectProcedure;

class TDoubleObjectHashMap$2 implements TDoubleObjectProcedure {
   private boolean first;
   // $FF: synthetic field
   final TDoubleObjectHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TDoubleObjectHashMap$2(TDoubleObjectHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(double var1, Object var3) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(",");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var3);
      return true;
   }
}
