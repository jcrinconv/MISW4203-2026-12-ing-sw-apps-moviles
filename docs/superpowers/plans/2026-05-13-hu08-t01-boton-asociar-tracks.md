# HU08-T01: Botón "Asociar tracks" Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Agregar un FAB en `AlbumDetailFragment` que navega a `TrackAssociateFragment` (pantalla stub), pasando el `album_id`.

**Architecture:** Se agrega un `FloatingActionButton` dentro del `CoordinatorLayout` existente en `fragment_album_detail.xml`. Al hacer click navega vía NavController a un nuevo `TrackAssociateFragment` (stub, similar a `AlbumCreateFragment`). Los tests Espresso verifican visibilidad del FAB y la navegación.

**Tech Stack:** Kotlin, Android Navigation Component, Espresso + MockWebServer, Material Design FAB

---

## Mapa de archivos

| Acción | Archivo |
|--------|---------|
| Modify | `app/src/main/res/values/strings.xml` |
| Modify | `app/src/main/res/navigation/nav_graph.xml` |
| Modify | `app/src/main/res/layout/fragment_album_detail.xml` |
| Create | `app/src/main/res/layout/fragment_track_associate.xml` |
| Create | `app/src/main/java/com/misw/app/ui/albums/TrackAssociateFragment.kt` |
| Create | `app/src/main/java/com/misw/app/viewmodel/TrackAssociateViewModel.kt` |
| Modify | `app/src/main/java/com/misw/app/ui/albums/AlbumDetailFragment.kt` |
| Create | `app/src/androidTest/java/com/misw/app/HU08TrackAssociateTest.kt` |

---

## Task 1: Agregar string y destino de navegación

**Files:**
- Modify: `app/src/main/res/values/strings.xml`
- Modify: `app/src/main/res/navigation/nav_graph.xml`

- [ ] **Step 1: Agregar string para content description del FAB**

En `app/src/main/res/values/strings.xml`, agregar dentro de `<resources>`:

```xml
    <string name="associate_tracks_fab_desc">Asociar tracks</string>
```

- [ ] **Step 2: Agregar destino y acción en nav_graph.xml**

En `app/src/main/res/navigation/nav_graph.xml`, reemplazar el bloque del `albumDetailFragment`:

```xml
    <fragment
        android:id="@+id/albumDetailFragment"
        android:name="com.misw.app.ui.albums.AlbumDetailFragment"
        android:label="Detalle del álbum"
        tools:layout="@layout/fragment_album_detail">
        <argument
            android:name="album_id"
            app:argType="integer" />
        <action
            android:id="@+id/action_albumDetailFragment_to_trackAssociateFragment"
            app:destination="@id/trackAssociateFragment" />
    </fragment>

    <fragment
        android:id="@+id/trackAssociateFragment"
        android:name="com.misw.app.ui.albums.TrackAssociateFragment"
        android:label="Asociar track"
        tools:layout="@layout/fragment_track_associate">
        <argument
            android:name="album_id"
            app:argType="integer" />
    </fragment>
```

---

## Task 2: Crear TrackAssociateFragment (stub) y ViewModel

**Files:**
- Create: `app/src/main/res/layout/fragment_track_associate.xml`
- Create: `app/src/main/java/com/misw/app/viewmodel/TrackAssociateViewModel.kt`
- Create: `app/src/main/java/com/misw/app/ui/albums/TrackAssociateFragment.kt`

- [ ] **Step 1: Crear layout fragment_track_associate.xml**

Crear `app/src/main/res/layout/fragment_track_associate.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical"
    android:padding="24dp">

    <TextView
        android:id="@+id/tvAssociateTrackTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/associate_tracks_fab_desc"
        android:textSize="24sp"
        android:textStyle="bold" />

</LinearLayout>
```

- [ ] **Step 2: Crear TrackAssociateViewModel.kt**

Crear `app/src/main/java/com/misw/app/viewmodel/TrackAssociateViewModel.kt`:

```kotlin
package com.misw.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class TrackAssociateViewModel(application: Application) : AndroidViewModel(application)
```

- [ ] **Step 3: Crear TrackAssociateFragment.kt**

Crear `app/src/main/java/com/misw/app/ui/albums/TrackAssociateFragment.kt`:

```kotlin
package com.misw.app.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.misw.app.databinding.FragmentTrackAssociateBinding
import com.misw.app.viewmodel.TrackAssociateViewModel

class TrackAssociateFragment : Fragment() {

    private val viewModel: TrackAssociateViewModel by viewModels()
    private var _binding: FragmentTrackAssociateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackAssociateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

---

## Task 3: Agregar FAB al layout y conectar navegación

**Files:**
- Modify: `app/src/main/res/layout/fragment_album_detail.xml`
- Modify: `app/src/main/java/com/misw/app/ui/albums/AlbumDetailFragment.kt`

- [ ] **Step 1: Agregar FAB en fragment_album_detail.xml**

En `app/src/main/res/layout/fragment_album_detail.xml`, agregar el FAB como último hijo del `CoordinatorLayout` (antes del `</androidx.coordinatorlayout.widget.CoordinatorLayout>`):

```xml
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabAssociateTracks"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="16dp"
        android:contentDescription="@string/associate_tracks_fab_desc"
        android:src="@android:drawable/ic_input_add"
        app:layout_behavior="com.google.android.material.behavior.HideBottomViewOnScrollBehavior"
        app:layout_anchor="@id/albumDetailFragment"
        app:tint="@color/white" />
```

> **Nota:** Si el atributo `app:layout_anchor` genera error de lint (el id no existe en el layout), eliminarlo — el FAB se posicionará por `layout_gravity` dentro del CoordinatorLayout.

El FAB correcto sin anchor para este layout es:

```xml
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabAssociateTracks"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="16dp"
        android:contentDescription="@string/associate_tracks_fab_desc"
        android:src="@android:drawable/ic_input_add"
        app:tint="@color/white" />
```

- [ ] **Step 2: Conectar FAB en AlbumDetailFragment.kt**

En `AlbumDetailFragment.kt`, dentro de `onViewCreated`, agregar después de la línea `val albumId = arguments?.getInt("album_id") ?: 100`:

```kotlin
        binding.fabAssociateTracks.setOnClickListener {
            val id = arguments?.getInt("album_id") ?: 100
            val bundle = android.os.Bundle().apply { putInt("album_id", id) }
            findNavController().navigate(R.id.action_albumDetailFragment_to_trackAssociateFragment, bundle)
        }
```

También agregar los imports faltantes al inicio del archivo:

```kotlin
import androidx.navigation.fragment.findNavController
```

---

## Task 4: Escribir tests Espresso

**Files:**
- Create: `app/src/androidTest/java/com/misw/app/HU08TrackAssociateTest.kt`

- [ ] **Step 1: Crear HU08TrackAssociateTest.kt con setup/teardown**

```kotlin
package com.misw.app

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.recyclerview.widget.RecyclerView
import com.misw.app.network.CacheManager
import com.misw.app.network.EspressoIdlingResource
import com.misw.app.network.RetrofitClient
import com.misw.app.ui.MainActivity
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matchers.hasDescendant
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class HU08TrackAssociateTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        CacheManager.getInstance(InstrumentationRegistry.getInstrumentation().targetContext).clearCache()

        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: return MockResponse().setResponseCode(404)
                return when {
                    path == "/albums" -> MockResponse().setResponseCode(200).setBody(
                        """[{"id":1,"name":"Test Album","cover":"https://picsum.photos/200","releaseDate":"2020-01-01T00:00:00.000Z","description":"Desc","genre":"Rock","recordLabel":"EMI"}]"""
                    )
                    path == "/albums/1" -> MockResponse().setResponseCode(200).setBody(
                        """{"id":1,"name":"Test Album","cover":"https://picsum.photos/200","releaseDate":"2020-01-01T00:00:00.000Z","description":"Desc","genre":"Rock","recordLabel":"EMI","tracks":[]}"""
                    )
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
        mockWebServer.dispatcher = dispatcher
        mockWebServer.start(0)
        RetrofitClient.setBaseUrl(mockWebServer.url("/").toString())
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        RetrofitClient.setBaseUrl(BuildConfig.BASE_URL)
    }

    private fun navigateToAlbumDetail() {
        onView(withId(R.id.include_albums)).perform(click())
        onView(withId(R.id.rvAlbumList)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText("Test Album")), click()
            )
        )
    }

    @Test
    fun testAssociateTracksFabIsVisible() {
        navigateToAlbumDetail()
        onView(withId(R.id.fabAssociateTracks)).check(matches(isDisplayed()))
    }

    @Test
    fun testAssociateTracksFabNavigatesToAssociateScreen() {
        navigateToAlbumDetail()
        onView(withId(R.id.fabAssociateTracks)).perform(click())
        onView(withId(R.id.tvAssociateTrackTitle)).check(matches(isDisplayed()))
    }
}
```

- [ ] **Step 2: Verificar que el proyecto compila**

Desde Android Studio: Build → Make Project, o desde terminal:
```bash
./gradlew assembleDebug assembleAndroidTest
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Ejecutar los tests**

```bash
./gradlew connectedAndroidTest --tests "com.misw.app.HU08TrackAssociateTest"
```
Expected: 2 tests passed — `testAssociateTracksFabIsVisible` y `testAssociateTracksFabNavigatesToAssociateScreen`

---

## Self-Review

**Spec coverage:**
- ✅ Botón FAB en AlbumDetailFragment (Task 3)
- ✅ Navegación a TrackAssociateFragment (Task 3 step 2 + Task 1 step 2)
- ✅ Pantalla destino stub (Task 2)
- ✅ Tests: visibilidad del FAB + navegación (Task 4)

**Placeholders:** Ninguno — todo el código está completo.

**Consistencia de tipos:**
- `album_id` como `Int` en argumentos, `bundle.putInt`, y `arguments?.getInt` — consistente.
- `fabAssociateTracks` en layout y en Fragment — consistente.
- `tvAssociateTrackTitle` en layout y en test — consistente.
- `action_albumDetailFragment_to_trackAssociateFragment` en nav_graph y en Fragment — consistente.
