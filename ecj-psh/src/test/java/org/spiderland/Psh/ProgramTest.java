/*
 * Copyright 2009-2010 Jon Klein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spiderland.Psh;

import junit.framework.TestCase;

public class ProgramTest extends TestCase {
	public void testEquals() throws Exception {
		// Equality testing of nested programs 

		Program p = new Program(), q = new Program(), r = new Program();

		p.Parse( "( 1.0 ( TEST 2 ( 3 ) ) )" );
		q.Parse( "( 1.0 ( TEST 2 ( 3 ) ) )" );
		r.Parse( "( 2.0 ( TEST 2 ( 3 ) ) )" );

		assertFalse( p.equals( r ) );
		assertTrue( p.equals( q ) );
	}

	public void testParse() throws Exception {
		// Parse a program, and then re-parse its string representation.
		// They should be equal.

		Program p = new Program(), q = new Program();
		String program = "(1(2) (3) TEST TEST (2 TEST))";

		p.Parse( program );
		q.Parse( p.toString() );

		assertTrue( p.equals( q ) );
	}

	public void testSubtreeFetch() throws Exception {
		Program p = new Program();
		p.Parse( "( 2.0 ( TEST 2 ( 3 ) ) )" );

		assertTrue( true );
	}

	public void testSubtreeReplace() throws Exception {
		Program p = new Program();
		Program q = new Program();

		p.Parse( "( 2.0 ( TEST 2 ( 3 ) ) )" );

		p.ReplaceSubtree( 0, 3 );
		p.ReplaceSubtree( 2, "TEST2" );
		p.ReplaceSubtree( 3, new Program( "( X )" ) );

		System.out.println( p );

		q.Parse( "( 3 ( TEST2 ( X ) ( 3 ) ) )" );

		assertTrue( q.equals( p ) );
	}
	
	public void testProgramsize() throws Exception {
		Program p = new Program();
		
		p.Parse( "(  A B C (1 2 3) A (4 5 C (F))  )" );
		assertEquals(14, p.programsize());
	}
	
	public void testProgramsizeWithStart() throws Exception {
		Program p = new Program();
		
		p.Parse( "(  A B C (1 2 3) A (4 5 C (F))  )" );
		
		assertEquals(6, p.size());
		
		assertEquals(6, p.programsize(-1));
		assertEquals(7, p.programsize(-2));
		
		assertEquals(14, p.programsize(0));
		assertEquals(13, p.programsize(1));
		assertEquals(12, p.programsize(2));
		assertEquals(11, p.programsize(3));
		assertEquals(7, p.programsize(4));
		
		assertEquals(6, p.programsize(5));
		
		assertEquals(0, p.programsize(6));	
	}
	
	public void testProgramsizeWithStartAndRange() throws Exception {
		Program p = new Program();
		
		p.Parse( "(  A B C (1 2 3) A (4 5 C (F))  )" );
		
		assertEquals(6, p.size());
		
		assertEquals(6, p.programsize(-1, 1));
		assertEquals(1, p.programsize(-2, 1));
		assertEquals(7, p.programsize(-2, 2));
		
		assertEquals(0, p.programsize(0, 0));
		assertEquals(0, p.programsize(0, -1));
		assertEquals(1, p.programsize(0, 1));
		assertEquals(2, p.programsize(0, 2));
		assertEquals(2, p.programsize(1, 2));
		assertEquals(6, p.programsize(1, 3));
		
		assertEquals(14, p.programsize(0, 3000));
		assertEquals(6, p.programsize(-1, 123));
		assertEquals(7, p.programsize(-2, 234));
		
		assertEquals(14, p.programsize(0, 456));
		assertEquals(13, p.programsize(1, 567));
		assertEquals(12, p.programsize(2, 34));
		assertEquals(11, p.programsize(3, 565));
		assertEquals(7, p.programsize(4, 56));
	}
	
	public void testCopyWithStartPoint() throws Exception {
		Program p = new Program();
		p.Parse( "(  A B C (1 2 3) A (4 5 C (F))  )" );
		
		assertEquals(new Program("( A B C (1 2 3) A (4 5 C (F)) )"),p.Copy(0));
		assertEquals(new Program("( B C (1 2 3) A (4 5 C (F)) )"),p.Copy(1));
		assertEquals(new Program("( (1 2 3) A (4 5 C (F)) )"),p.Copy(3));
		assertEquals(new Program("( A (4 5 C (F)) )"),p.Copy(-2));
		assertEquals(new Program("( (4 5 C (F)) )"),p.Copy(-1));
		assertEquals(new Program("( )"),p.Copy(234));
	}

	public void testCopyWithStartPointAndRange() throws Exception {
		Program p = new Program();
		p.Parse( "(  A B C (1 2 3) A (4 5 C (F))  )" );
		
		assertEquals(new Program("( (4 5 C (F)) )"),p.Copy(-1, 1));
		assertEquals(new Program("( (4 5 C (F)) )"),p.Copy(-1, 2));
		
		assertEquals(new Program("( A )"),p.Copy(-2, 1));
		assertEquals(new Program("( A (4 5 C (F)) )"),p.Copy(-2, 2));
		assertEquals(new Program("( A (4 5 C (F)) )"),p.Copy(-2, 3));
		
		assertEquals(new Program("( A B )"),p.Copy(0, 2));
		assertEquals(new Program("( B C )"),p.Copy(1, 2));
		assertEquals(new Program("( C (1 2 3))"),p.Copy(2, 2));
		assertEquals(new Program("( A B C (1 2 3) A )"),p.Copy(0, 5));

		assertEquals(new Program("( A B C (1 2 3) A (4 5 C (F)) )"),p.Copy(0, 6));
		assertEquals(new Program("( A B C (1 2 3) A (4 5 C (F)) )"),p.Copy(0, 3452345));
		assertEquals(new Program("( B C (1 2 3) A (4 5 C (F)) )"),p.Copy(1, 234534));
		assertEquals(new Program("( (1 2 3) A (4 5 C (F)) )"),p.Copy(3, 3455));
		assertEquals(new Program("( A (4 5 C (F)) )"),p.Copy(-2, 3456));
		assertEquals(new Program("( (4 5 C (F)) )"),p.Copy(-1, 345));
		assertEquals(new Program("( )"),p.Copy(234, 4565));		
	}
	
}
