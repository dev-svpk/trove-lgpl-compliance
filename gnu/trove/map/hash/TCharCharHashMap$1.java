package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TCharCharProcedure;

class TCharCharHashMap$1 implements TCharCharProcedure {
   private boolean first;
   // $FF: synthetic field
   final TCharCharHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TCharCharHashMap$1(TCharCharHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(char var1, char var2) {
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
