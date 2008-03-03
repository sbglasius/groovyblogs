class BlogTagLibTests extends GroovyTestCase {

    void testNiceDate() {

        // create taglib with mocked "out"
        StringWriter out = new StringWriter()
        BlogTagLib.metaClass.out = out
        BlogTagLib btl = new BlogTagLib()


        Date now = new Date()

        btl.niceDate(date: now)

        assertEquals(
            new java.text.SimpleDateFormat("EEE, d MMM yyyy HH:mm").format(now),
            out.toString()
        )

    }

    void testDateFromNow() {

        // create taglib with mocked "out"
        StringWriter out = new StringWriter()
        BlogTagLib.metaClass.out = out
        BlogTagLib btl = new BlogTagLib()

        Calendar cal = Calendar.getInstance()

        btl.dateFromNow(date: cal.time)

        assertEquals "Right now", out.toString()

        // reset "out" buffer
        out.getBuffer().setLength(0)

        cal.add(Calendar.HOUR, -1)
        btl.dateFromNow(date: cal.time)

        assertEquals "1 hour ago", out.toString()

    }
}
