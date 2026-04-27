package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TFloatByteProcedure;

class TFloatByteHashMap$1 implements TFloatByteProcedure {
   private boolean first;
   // $FF: synthetic field
   final TFloatByteHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TFloatByteHashMap$1(TFloatByteHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(float var1, byte var2) {
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
