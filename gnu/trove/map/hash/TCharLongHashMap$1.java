package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TCharLongProcedure;

class TCharLongHashMap$1 implements TCharLongProcedure {
   private boolean first;
   // $FF: synthetic field
   final TCharLongHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TCharLongHashMap$1(TCharLongHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(char var1, long var2) {
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
