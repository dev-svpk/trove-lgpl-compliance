package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TByteCharProcedure;

class TByteCharHashMap$1 implements TByteCharProcedure {
   private boolean first;
   // $FF: synthetic field
   final TByteCharHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TByteCharHashMap$1(TByteCharHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(byte var1, char var2) {
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
