package org.litesoft.student;

import org.junit.Test;

import static org.junit.Assert.*;

public class LegacyStudentFilterTest {

  @Test
  public void isStudentID() {
    LegacyStudentFilter zFilter = new LegacyStudentFilter();

    assertFalse( zFilter.isStudentID( "0" ) ); // Zero Not allowed
    assertFalse( zFilter.isStudentID( "00" ) ); // Zero Not Allowed
    assertFalse( zFilter.isStudentID( "002" ) ); // Not 2 digits
    assertFalse( zFilter.isStudentID( "A" ) ); // Not digit
    assertFalse( zFilter.isStudentID( "A2" ) ); // not digits

//    assertTrue( zFilter.isStudentID( "1" ) );
    assertTrue( zFilter.isStudentID( "2" ) );
    assertTrue( zFilter.isStudentID( "3" ) );
    assertTrue( zFilter.isStudentID( "4" ) );
    assertTrue( zFilter.isStudentID( "5" ) );
    assertTrue( zFilter.isStudentID( "6" ) );
    assertTrue( zFilter.isStudentID( "7" ) );
    assertTrue( zFilter.isStudentID( "8" ) );
    assertTrue( zFilter.isStudentID( "9" ) );
    assertTrue( zFilter.isStudentID( "10" ) );
    assertTrue( zFilter.isStudentID( "11" ) );
    assertTrue( zFilter.isStudentID( "12" ) );
    assertTrue( zFilter.isStudentID( "13" ) );
    assertTrue( zFilter.isStudentID( "14" ) );
    assertTrue( zFilter.isStudentID( "15" ) );
    assertTrue( zFilter.isStudentID( "16" ) );
    assertTrue( zFilter.isStudentID( "17" ) );
    assertTrue( zFilter.isStudentID( "18" ) );
    assertTrue( zFilter.isStudentID( "19" ) );
    assertTrue( zFilter.isStudentID( "20" ) );
    assertTrue( zFilter.isStudentID( "21" ) );
    assertTrue( zFilter.isStudentID( "22" ) );
    assertTrue( zFilter.isStudentID( "23" ) );
    assertTrue( zFilter.isStudentID( "24" ) );
    assertTrue( zFilter.isStudentID( "25" ) );

    assertFalse( zFilter.isStudentID( "26" ) );
    assertFalse( zFilter.isStudentID( "29" ) );
    assertFalse( zFilter.isStudentID( "30" ) );
    assertFalse( zFilter.isStudentID( "39" ) );
    assertFalse( zFilter.isStudentID( "40" ) );
    assertFalse( zFilter.isStudentID( "49" ) );
    assertFalse( zFilter.isStudentID( "50" ) );
    assertFalse( zFilter.isStudentID( "59" ) );
    assertFalse( zFilter.isStudentID( "60" ) );
    assertFalse( zFilter.isStudentID( "69" ) );
    assertFalse( zFilter.isStudentID( "70" ) );
    assertFalse( zFilter.isStudentID( "79" ) );
    assertFalse( zFilter.isStudentID( "80" ) );
    assertFalse( zFilter.isStudentID( "89" ) );
    assertFalse( zFilter.isStudentID( "90" ) );
    assertFalse( zFilter.isStudentID( "99" ) );
  }
}