package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase, MuxLookup }
import chisel3.experimental.MultiIOModule

import lookup._
import Op0Select._
import Op1Select._
import ImmFormat._
import YN._
import DonotCare.DC


class InstructionDecode extends MultiIOModule {



    val io = IO(new Bundle {
        val in=new Bundle{
            val w_rd=Input(UInt(1.W))
            val ins=Input(new Instruction)
        }
        
        val out=new Bundle{
            val op_0=Output(UInt(32.W))
            val op_1=Output(UInt(32.W))
            
            val alu_op=Output(UInt(4.W))
            val rd=Output(UInt(5.W))
            val w_rd=Output(UInt(1.W))
        }
        
        val waddr=Input(UInt(32.W))
        val wdata=Input(UInt(32.W))
        
    })

    val registers = Module(new Registers)
    val decoder   = Module(new Decoder)
    
    val ins=io.in.ins
    
    io.out.rd:=ins.registerRd
    
    val imm_sel = Array(
        ITYPE  -> ins.immediateIType,
        UTYPE  -> ins.immediateUType.asUInt,
        // STYPE  -> ins.immediateIType.asTypeOf(SInt(32.W)),
    )
    
    val op_0_sel = Array(
        RS1    -> registers.io.readData1,
        PC     -> 0.U,//fix me
        Z      -> 0.U,
    )
    
    val imm=MuxLookup(decoder.io.imm_type,1919810.U,imm_sel)
    
    val op_1_sel = Array(
        RS2    -> registers.io.readData2, 
        IMM    -> imm,
    )
    
    
    io.out.op_0:=MuxLookup(decoder.io.op_0_type,114514.U,op_0_sel)
    
    io.out.op_1:=MuxLookup(decoder.io.op_1_type,114514.U,op_1_sel)
    
    registers.io.readAddress1 := ins.registerRs1 
    registers.io.readAddress2 := ins.registerRs2
    registers.io.writeEnable  := io.in.w_rd
    registers.io.writeAddress := io.waddr 
    registers.io.writeData    := io.wdata 
    
    io.out.alu_op:=decoder.io.alu_op
    io.out.w_rd:=decoder.io.ctrl_signal.regWrite
    
    decoder.io.ins := ins    
    // Don't touch the test harness
    val testHarness = IO(
        new Bundle {
            val registerSetup = Input(new RegisterSetupSignals)
            val registerPeek  = Output(UInt(32.W))
            val testUpdates   = Output(new RegisterUpdates)
    })
    testHarness.registerPeek    := registers.io.readData1
    testHarness.testUpdates     := registers.testHarness.testUpdates
    registers.testHarness.setup := testHarness.registerSetup
}



