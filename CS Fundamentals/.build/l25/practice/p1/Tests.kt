import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import org.junit.Assert.assertEquals
import org.junit.Test

class AnnotationTest {
    @Test fun `columns are read from the fields`() {
        assertEquals(mapOf("email" to "user_email", "name" to "display_name"), columns(Account::class.java))
    }

    @Test fun `audited methods are visible at runtime`() {
        assertEquals(listOf("delete", "save"), auditedMethods(Account::class.java))
    }

    @Test fun `Column may be put on fields only`() {
        val target = Column::class.java.getAnnotation(java.lang.annotation.Target::class.java)
        assertEquals("@Column's allowed targets", listOf(ElementType.FIELD), target?.value?.toList())
    }

    @Test fun `Secret never reaches the class file`() {
        val retention = Secret::class.java.getAnnotation(Retention::class.java)?.value
        assertEquals("@Secret's retention", RetentionPolicy.SOURCE, retention)
    }
}
