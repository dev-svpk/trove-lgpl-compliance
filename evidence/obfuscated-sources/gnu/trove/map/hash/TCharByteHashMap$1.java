package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.j;

class TCharByteHashMap$1 implements j {
   private boolean first;
   // $FF: synthetic field
   final TCharByteHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TCharByteHashMap$1(TCharByteHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(char var1, byte var2) {
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
