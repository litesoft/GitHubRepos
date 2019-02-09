package org.litesoft.student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.litesoft.annotations.Nullable;
import org.litesoft.annotations.Significant;

@SuppressWarnings({"WeakerAccess", "unused", "UnusedReturnValue"})
public class Seats {
  public static boolean isValidSeatID( String pSeatID ) {
    return (pSeatID != null) && (pSeatID.length() == 2) && // Left to Right!
           isValidSeat( pSeatID.charAt( 0 ), pSeatID.charAt( 1 ) );
  }

  public static boolean isValidSeat( char pColumn, char pRow ) {
    return isValidSeatColumn( pColumn ) && isValidSeatRow( pRow );
  }

  public static boolean isValidSeatColumn( char pColumn ) {
    return ('A' <= pColumn) && (pColumn <= 'Z');
  }

  public static boolean isValidSeatRow( char pRow ) {
    return ('1' <= pRow) && (pRow <= '9');
  }

  private final Set<String> mSeats;

  private Seats( Set<String> pSeats ) {
    TreeSet<String> zSeatIDs = new TreeSet<>( pSeats );
    mSeats = Collections.unmodifiableSet( zSeatIDs );
  }

  public boolean isEmpty() {
    return mSeats.isEmpty();
  }

  public int count() {
    return mSeats.size();
  }

  public boolean hasSeat(@Nullable String pSeat) {
    return (pSeat != null) && mSeats.contains( pSeat ); // Left to Right
  }

  public List<String> getSeats() {
    return new ArrayList<>( mSeats );
  }

  public static class Builder {
    private final Set<String> mSeats = new TreeSet<>();

    public Builder add( char pColumn, char pRow ) {
      String zSeat = "" + pColumn + pRow;
      if ( !isValidSeat( pColumn, pRow ) ) {
        throw new IllegalArgumentException( "Invalid Seat Reference '" + zSeat +
                                            "' from Column (" + (int)pColumn +
                                            ") and Row (" + (int)pRow +
                                            ")" );
      }
      mSeats.add( zSeat );
      return this;
    }

    /**
     * Add seat options, where options are separated by a comma (',').
     * <p>
     * A seat option is in one of the forms (where 'C' indicates a valid Column Reference and 'R' represents a valid Row reference):
     * <ul>CR</ul>
     * <ul>CxR-R</ul>
     * <ul>C-CxR</ul>
     * <ul>C-CxR-R</ul>
     */
    public Builder add( @Nullable String pSeatOptions ) {
      pSeatOptions = Significant.ConstrainTo.valueOrEmpty( pSeatOptions );
      for ( int zCommaAt = pSeatOptions.indexOf( ',' ); zCommaAt != -1; zCommaAt = pSeatOptions.indexOf( ',' ) ) {
        addSeatOption( pSeatOptions.substring( 0, zCommaAt ).trim() );
        pSeatOptions = pSeatOptions.substring( zCommaAt + 1 );
      }
      addSeatOption( pSeatOptions.trim() );
      return this;
    }

    public Seats build() {
      return new Seats( mSeats );
    }

    /**
     * Add seat option.
     * <p>
     * A seat option is in one of the forms (where 'C' indicates a valid Column Reference and 'R' represents a valid Row reference):
     * <ul>CR</ul>
     * <ul>CxR-R</ul>
     * <ul>C-CxR</ul>
     * <ul>C-CxR-R</ul>
     */
    private void addSeatOption( String pOption ) {
      int zLength = pOption.length();
      if ( zLength != 0 ) {
        Predicate<String> zAddOption = null;
        if ( zLength == 2 ) {
          zAddOption = this::addRegular;
        } else if ( (zLength == 5) || (zLength == 7) ) {
          zAddOption = this::addRange;
        }
        if ( (zAddOption != null) && zAddOption.test( pOption ) ) {
          return;
        }
        throw new IllegalArgumentException( "Seat Option '" + pOption + "' does not conform to any of the 4 valid options" );
      }
    }

    private boolean addRegular( String pOption ) {
      return addValid( pOption.charAt( 0 ), pOption.charAt( 1 ) );
    }

    private boolean addRange( String pOption ) {
      int zXat = pOption.indexOf( 'x' );
      if ( (zXat != 1) && (zXat != 3) ) {
        return false;
      }
      CharPair zColumns = CharPair.from( pOption.substring( 0, zXat ) );
      CharPair zRows = CharPair.from( pOption.substring( zXat + 1 ) );
      return (zColumns != null) && (zRows != null) && // Left to Right!
             addCells( zColumns, zRows );
    }

    private boolean addCells( CharPair pColumns, CharPair pRows ) {
      boolean zRowsAdded = addRows( pColumns.first(), pRows );
      while ( zRowsAdded && pColumns.hasNext() ) {
        zRowsAdded = addRows( pColumns.next(), pRows );
      }
      return zRowsAdded;
    }

    private boolean addRows( char pColumn, CharPair pRows ) {
      boolean zCellsAdded = addValid( pColumn, pRows.first() );
      while ( zCellsAdded && pRows.hasNext() ) {
        zCellsAdded = addValid( pColumn, pRows.next() );
      }
      return zCellsAdded;
    }

    private boolean addValid( char pColumn, char pRow ) {
      if ( !isValidSeat( pColumn, pRow ) ) {
        return false;
      }
      add( pColumn, pRow );
      return true;
    }
  }

  private static class CharPair {
    private final char mFrom, mThru;
    private char mNext;

    public CharPair( char pFrom, char pThru ) {
      mFrom = pFrom;
      mThru = pThru;
      mNext = mFrom;
    }

    public static CharPair from( String pStr ) {
      char z1stChar = pStr.charAt( 0 );
      if ( pStr.length() == 1 ) {
        return new CharPair( z1stChar, z1stChar );
      }
      // Must be 3 long
      if ( pStr.charAt( 1 ) != '-' ) {
        return null;
      }
      char z2ndChar = pStr.charAt( 2 );
      return (z2ndChar < z1stChar) ?
             new CharPair( z2ndChar, z1stChar ) :
             new CharPair( z1stChar, z2ndChar );
    }

    public char first() {
      mNext = mFrom;
      return next();
    }

    private char next() {
      if ( !hasNext() ) {
        throw new IllegalStateException( "No More characters" );
      }
      return mNext++;
    }

    public boolean hasNext() {
      return (mNext <= mThru);
    }
  }
}
