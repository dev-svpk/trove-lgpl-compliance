package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TDoubleByteProcedure;

class TDoubleByteHashMap$1 implements TDoubleByteProcedure {
   private boolean first;
   // $FF: synthetic field
   final TDoubleByteHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TDoubleByteHashMap$1(TDoubleByteHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(double var1, byte var3) {
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
