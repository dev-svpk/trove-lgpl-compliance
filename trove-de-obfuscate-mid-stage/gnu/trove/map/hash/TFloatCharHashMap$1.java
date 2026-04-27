package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TFloatCharProcedure;

class TFloatCharHashMap$1 implements TFloatCharProcedure {
   private boolean first;
   // $FF: synthetic field
   final TFloatCharHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TFloatCharHashMap$1(TFloatCharHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(float var1, char var2) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var2);
      return true;
   }
}
