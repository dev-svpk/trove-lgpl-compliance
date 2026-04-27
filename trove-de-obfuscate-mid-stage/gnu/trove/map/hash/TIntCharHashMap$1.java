package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TIntCharProcedure;

class TIntCharHashMap$1 implements TIntCharProcedure {
   private boolean first;
   // $FF: synthetic field
   final TIntCharHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TIntCharHashMap$1(TIntCharHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(int var1, char var2) {
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
