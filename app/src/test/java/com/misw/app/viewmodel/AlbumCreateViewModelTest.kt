package com.misw.app.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.misw.app.model.AlbumRequest
import com.misw.app.repository.album.AlbumRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.reflect.Field

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumCreateViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @MockK
    lateinit var application: Application

    @MockK
    lateinit var repository: AlbumRepository

    private lateinit var viewModel: AlbumCreateViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        
        viewModel = spyk(AlbumCreateViewModel(application))
        
        // Inyectar el repositorio mockeado usando reflexión ya que es privado y se instancia en el init
        val field: Field = AlbumCreateViewModel::class.java.getDeclaredField("repository")
        field.isAccessible = true
        field.set(viewModel, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando los campos estan vacios debe mostrar errores`() {
        viewModel.createAlbum("", "", "", "", 0, "", "", "")
        
        assertEquals("El nombre es obligatorio", viewModel.nameError.value)
        assertEquals("La URL de la portada es obligatoria", viewModel.coverError.value)
        assertEquals("La descripción es obligatoria", viewModel.descriptionError.value)
        assertEquals("Fecha incompleta", viewModel.dateError.value)
    }

    @Test
    fun `cuando la fecha es invalida debe mostrar error`() {
        viewModel.createAlbum("Name", "https://cover.com", "Desc", "32", 0, "2024", "Rock", "EMI")
        assertEquals("Fecha no válida", viewModel.dateError.value)
    }

    @Test
    fun `cuando la creacion es exitosa debe actualizar isSuccess`() {
        val observer = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.isSuccess.observeForever(observer)

        coEvery { repository.createAlbum(any()) } returns mockk()

        viewModel.createAlbum(
            "Test Album", 
            "https://example.com/image.jpg", 
            "Description", 
            "10", 
            4, // Mayo
            "2023", 
            "Rock", 
            "EMI"
        )

        coVerify { repository.createAlbum(any()) }
        assertEquals(true, viewModel.isSuccess.value)
        assertNull(viewModel.nameError.value)
        
        viewModel.isSuccess.removeObserver(observer)
    }

    @Test
    fun `cuando el repositorio falla debe mostrar error`() {
        coEvery { repository.createAlbum(any()) } throws Exception("Network Error")

        viewModel.createAlbum(
            "Test Album", 
            "https://example.com/image.jpg", 
            "Description", 
            "10", 
            4, 
            "2023", 
            "Rock", 
            "EMI"
        )

        assertEquals("Error al crear el álbum", viewModel.error.value)
        assertEquals(false, viewModel.isLoading.value)
    }
}
