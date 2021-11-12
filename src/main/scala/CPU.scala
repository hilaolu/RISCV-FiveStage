package FiveStage

import chisel3.core.ExplicitCompileOptions.Strict
//todo avoid implicit truncation
import chisel3._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule
import chisel3.experimental._


class CPU extends MultiIOModule {

    val testHarness = IO(
        new Bundle {
            val setupSignals = Input(new SetupSignals)
            val testReadouts = Output(new TestReadouts)
            val regUpdates   = Output(new RegisterUpdates)
            val memUpdates   = Output(new MemUpdates)
            val currentPC    = Output(UInt(32.W))
        }
    )
  
    
    val ID  = Module(new InstructionDecode)
    val IF  = Module(new InstructionFetch)
    val EX  = Module(new Execute)
    val MEM = Module(new MemoryFetch)
    val WB  = Module(new WriteBack)
    
    val MEMR=Module(new MEMRegs)
    val EXR = Module(new EXRegs)
    val WBR=Module(new WBRegs)
    
    IF.io.stall:=ID.io.stall
    IF.io.decode_jump:=ID.io.jump
    IF.io.e_branch:=ID.io.e_branch
    IF.io.branch_offset:=ID.io.branch_offset
    
    ID.io.in.ins:=IF.io.ins
    ID.io.in.pc:=IF.io.pc
    ID.io.in.pc_4:=IF.io.pc_4
    
    EXR.io.out<>EX.io.in
    EXR.io.in<>ID.io.out
    
    MEMR.io.in<>EX.io.out
    MEMR.io.out<>MEM.io.in    
    
    WBR.io.in<>MEM.io.out
    WBR.io.out<>WB.io.in
    
    WB.io.out<>ID.io.wb_in
    WB.io.in<>WBR.io.out
    WB.io.mem_data:=MEM.io.mem_data
    
    
    ID.io.ex_in.reg_data:=EX.io.out.alu_data
    ID.io.ex_in.rd:=EXR.io.out.rd
    ID.io.ex_in.w_rd:=EXR.io.out.w_rd
    ID.io.ex_in.mem_op:=EXR.io.out.mem_op
    
    ID.io.mem_in.reg_data:=MEMR.io.out.alu_data
    ID.io.mem_in.rd:=MEMR.io.out.rd
    ID.io.mem_in.w_rd:=MEMR.io.out.w_rd
    ID.io.mem_in.mem_op:=MEMR.io.out.mem_op
    
    
    
    {
        /**
        *   Setup. You should not change this code
        */
        IF.testHarness.IMEMsetup     := testHarness.setupSignals.IMEMsignals
        ID.testHarness.registerSetup := testHarness.setupSignals.registerSignals
        MEM.testHarness.DMEMsetup    := testHarness.setupSignals.DMEMsignals
        
        testHarness.testReadouts.registerRead := ID.testHarness.registerPeek
        testHarness.testReadouts.DMEMread     := MEM.testHarness.DMEMpeek
        
        /**
            spying stuff
        */
        testHarness.regUpdates := ID.testHarness.testUpdates
        testHarness.memUpdates := MEM.testHarness.testUpdates
        testHarness.currentPC  := IF.testHarness.PC
    }
    
    
    
}
