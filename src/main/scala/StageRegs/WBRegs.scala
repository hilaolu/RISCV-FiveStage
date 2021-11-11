package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule

object WBRegs{
    class Contents extends Bundle{
        val reg_data=UInt(32.W)
        val rd=UInt(5.W)
        val w_rd=Bool()
        val mem_op=Bool()
    }
}

class WBRegs extends MultiIOModule {
        
    val io = IO(
        new Bundle {
            val in=Input(new WBRegs.Contents)            
            val out=Output(new WBRegs.Contents)            
    })
    
    val contents=Reg(new WBRegs.Contents)
    
    
    contents:=io.in
    io.out:=contents
    
    

}
