package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TDoubleCharProcedure;

class TDoubleCharHashMap$1 implements TDoubleCharProcedure {
   private boolean first;
   // $FF: synthetic field
   final TDoubleCharHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TDoubleCharHashMap$1(TDoubleCharHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(double var1, char var3) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var3);
      return true;
   }
}
