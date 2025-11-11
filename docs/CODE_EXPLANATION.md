[//]: # (# 📚 GIẢI THÍCH CHI TIẾT CODE - LAB BOOKING APP)

[//]: # ()
[//]: # (## 🏗️ KIẾN TRÚC TỔNG QUAN)

[//]: # ()
[//]: # (Ứng dụng Lab Booking được xây dựng theo kiến trúc **Clean Architecture** với các lớp:)

[//]: # (- **Presentation Layer**: UI, Activities, Fragments)

[//]: # (- **Domain Layer**: Models, Use Cases, Repository Interfaces)

[//]: # (- **Data Layer**: Repository Implementations, Firebase Services, Database)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (## 🔄 LUỒNG HOẠT ĐỘNG TỪ UI ĐẾN BACKEND)

[//]: # ()
[//]: # (### 1. KHỞI ĐỘNG ỨNG DỤNG)

[//]: # ()
[//]: # (#### **LabBookingApp.java** &#40;Application Class&#41;)

[//]: # (```java)

[//]: # (public class LabBookingApp extends Application {)

[//]: # (    @Override)

[//]: # (    public void onCreate&#40;&#41; {)

[//]: # (        super.onCreate&#40;&#41;;)

[//]: # (        FirebaseApp.initializeApp&#40;this&#41;; // Khởi tạo Firebase)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**Chức năng:**)

[//]: # (- Là class Application, chạy đầu tiên khi app khởi động)

[//]: # (- Khởi tạo Firebase để sử dụng các dịch vụ Firebase &#40;Auth, Firestore&#41;)

[//]: # (- Gọi `PreloadManager.shutdown&#40;&#41;` khi app tắt để dọn dẹp tài nguyên)

[//]: # ()
[//]: # (**Luồng:**)

[//]: # (1. Android System gọi `onCreate&#40;&#41;` khi app start)

[//]: # (2. Khởi tạo Firebase SDK)

[//]: # (3. App sẵn sàng cho các Activity khác)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (### 2. MÀN HÌNH ĐĂNG NHẬP)

[//]: # ()
[//]: # (#### **LoginActivity.java** &#40;Màn hình đăng nhập&#41;)

[//]: # ()
[//]: # (**Luồng hoạt động:**)

[//]: # ()
[//]: # (```)

[//]: # (User nhập email/password )

[//]: # (    ↓)

[//]: # (onCreate&#40;&#41; → initViews&#40;&#41; → setupGoogleSignIn&#40;&#41; → setListeners&#40;&#41;)

[//]: # (    ↓)

[//]: # (User click "Login")

[//]: # (    ↓)

[//]: # (attemptLogin&#40;&#41;)

[//]: # (    ↓)

[//]: # (ValidationUtils.isValidEmail&#40;&#41; và isValidPassword&#40;&#41;)

[//]: # (    ↓)

[//]: # (signInWithEmailAndPassword&#40;&#41;)

[//]: # (    ↓)

[//]: # (AuthRepository.login&#40;&#41; → FirebaseAuthService.login&#40;&#41;)

[//]: # (    ↓)

[//]: # (Firebase Authentication API)

[//]: # (    ↓)

[//]: # (onComplete&#40;&#41; callback)

[//]: # (    ↓)

[//]: # (Thành công → NavigationManager.goToMain&#40;&#41;)

[//]: # (    ↓)

[//]: # (MainActivity được mở)

[//]: # (```)

[//]: # ()
[//]: # (**Chi tiết từng đoạn code:**)

[//]: # ()
[//]: # (**1. Khởi tạo &#40;onCreate&#41;:**)

[//]: # (```java)

[//]: # (protected void onCreate&#40;Bundle savedInstanceState&#41; {)

[//]: # (    setContentView&#40;R.layout.activity_login&#41;;)

[//]: # (    initLoadingOverlay&#40;&#41;; // Hiển thị loading overlay)

[//]: # (    initViews&#40;&#41;; // Tìm các view từ layout)

[//]: # (    setupGoogleSignIn&#40;&#41;; // Cấu hình Google Sign In)

[//]: # (    setListeners&#40;&#41;; // Gán event listeners)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**2. initViews&#40;&#41;:**)

[//]: # (```java)

[//]: # (private void initViews&#40;&#41; {)

[//]: # (    emailEditText = findViewById&#40;R.id.emailEditText&#41;;)

[//]: # (    passwordEditText = findViewById&#40;R.id.passwordEditText&#41;;)

[//]: # (    loginButton = findViewById&#40;R.id.loginButton&#41;;)

[//]: # (    // ...)

[//]: # (    authRepository = new AuthRepositoryImpl&#40;new FirebaseAuthService&#40;&#41;&#41;;)

[//]: # (    // Tạo repository với Firebase service)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**3. attemptLogin&#40;&#41;:**)

[//]: # (```java)

[//]: # (private void attemptLogin&#40;&#41; {)

[//]: # (    String email = emailEditText.getText&#40;&#41;.toString&#40;&#41;.trim&#40;&#41;;)

[//]: # (    String password = passwordEditText.getText&#40;&#41;.toString&#40;&#41;;)

[//]: # (    )
[//]: # (    // Validate email format)

[//]: # (    if &#40;!ValidationUtils.isValidEmail&#40;email&#41;&#41; {)

[//]: # (        emailEditText.setError&#40;"Invalid email"&#41;;)

[//]: # (        return;)

[//]: # (    })

[//]: # (    )
[//]: # (    // Validate password)

[//]: # (    if &#40;!ValidationUtils.isValidPassword&#40;password&#41;&#41; {)

[//]: # (        passwordEditText.setError&#40;"Invalid password"&#41;;)

[//]: # (        return;)

[//]: # (    })

[//]: # (    )
[//]: # (    showLoading&#40;&#41;; // Hiển thị loading)

[//]: # (    signInWithEmailAndPassword&#40;email, password&#41;;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**4. signInWithEmailAndPassword&#40;&#41;:**)

[//]: # (```java)

[//]: # (private void signInWithEmailAndPassword&#40;String email, String password&#41; {)

[//]: # (    authRepository.login&#40;email, password&#41;)

[//]: # (        .addOnCompleteListener&#40;this, task -> {)

[//]: # (            hideLoading&#40;&#41;;)

[//]: # (            if &#40;task.isSuccessful&#40;&#41;&#41; {)

[//]: # (                Toast.makeText&#40;this, "Login success", Toast.LENGTH_SHORT&#41;.show&#40;&#41;;)

[//]: # (                NavigationManager.goToMain&#40;this&#41;; // Chuyển sang MainActivity)

[//]: # (                finish&#40;&#41;; // Đóng LoginActivity)

[//]: # (            } else {)

[//]: # (                Toast.makeText&#40;this, "Login failed", Toast.LENGTH_SHORT&#41;.show&#40;&#41;;)

[//]: # (            })

[//]: # (        }&#41;;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**5. AuthRepositoryImpl.login&#40;&#41;:**)

[//]: # (```java)

[//]: # (@Override)

[//]: # (public Task<AuthResult> login&#40;String email, String password&#41; {)

[//]: # (    return authService.login&#40;email, password&#41;;)

[//]: # (    // Gọi FirebaseAuthService)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**6. FirebaseAuthService.login&#40;&#41;:**)

[//]: # (```java)

[//]: # (@Override)

[//]: # (public Task<AuthResult> login&#40;String email, String password&#41; {)

[//]: # (    return firebaseAuth.signInWithEmailAndPassword&#40;email, password&#41;;)

[//]: # (    // Gọi Firebase SDK)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**Google Sign In:**)

[//]: # (```java)

[//]: # (private void signInWithGoogle&#40;&#41; {)

[//]: # (    googleSignInClient.signOut&#40;&#41;.addOnCompleteListener&#40;this, task -> {)

[//]: # (        Intent signInIntent = googleSignInClient.getSignInIntent&#40;&#41;;)

[//]: # (        startActivityForResult&#40;signInIntent, RC_SIGN_IN&#41;;)

[//]: # (    }&#41;;)

[//]: # (})

[//]: # ()
[//]: # (@Override)

[//]: # (protected void onActivityResult&#40;int requestCode, int resultCode, Intent data&#41; {)

[//]: # (    if &#40;requestCode == RC_SIGN_IN&#41; {)

[//]: # (        GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent&#40;data&#41;.getResult&#40;&#41;;)

[//]: # (        firebaseAuthWithGoogle&#40;account.getIdToken&#40;&#41;&#41;;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (### 3. MÀN HÌNH CHÍNH &#40;MainActivity&#41;)

[//]: # ()
[//]: # (#### **MainActivity.java** &#40;Activity chính với Bottom Navigation&#41;)

[//]: # ()
[//]: # (**Luồng hoạt động:**)

[//]: # ()
[//]: # (```)

[//]: # (MainActivity.onCreate&#40;&#41;)

[//]: # (    ↓)

[//]: # (AuthRequiredActivity.onCreate&#40;&#41; → Kiểm tra đăng nhập)

[//]: # (    ↓)

[//]: # (LocaleUtils.applyLocale&#40;&#41; → Áp dụng ngôn ngữ)

[//]: # (    ↓)

[//]: # (ThemeUtils.applyTheme&#40;&#41; → Áp dụng theme)

[//]: # (    ↓)

[//]: # (PreloadManager.initialize&#40;&#41; → Preload Maps, Firebase)

[//]: # (    ↓)

[//]: # (setContentView&#40;R.layout.activity_main&#41;)

[//]: # (    ↓)

[//]: # (Khởi tạo BottomNavigationView)

[//]: # (    ↓)

[//]: # (Kiểm tra cart items → Lên lịch notification nếu có)

[//]: # (    ↓)

[//]: # (NavigationManager.showHome&#40;&#41; → Hiển thị HomeFragment)

[//]: # (    ↓)

[//]: # (BottomNavigationView listener → Chuyển fragment khi click)

[//]: # (```)

[//]: # ()
[//]: # (**Chi tiết code:**)

[//]: # ()
[//]: # (**1. onCreate&#40;&#41;:**)

[//]: # (```java)

[//]: # (protected void onCreate&#40;Bundle savedInstanceState&#41; {)

[//]: # (    LocaleUtils.applyLocale&#40;this&#41;; // Áp dụng locale &#40;ngôn ngữ&#41;)

[//]: # (    ThemeUtils.applyTheme&#40;this&#41;; // Áp dụng theme &#40;dark/light&#41;)

[//]: # (    PreloadManager.getInstance&#40;&#41;.initialize&#40;this&#41;; // Preload resources)

[//]: # (    super.onCreate&#40;savedInstanceState&#41;;)

[//]: # (    setContentView&#40;R.layout.activity_main&#41;;)

[//]: # (    )
[//]: # (    BottomNavigationView bottomNav = findViewById&#40;R.id.bottom_navigation&#41;;)

[//]: # (    cartManager = CartManager.getInstance&#40;this&#41;;)

[//]: # (    )
[//]: # (    // Kiểm tra cart và lên lịch notification)

[//]: # (    List<CartItem> cartItems = cartManager.getCartItems&#40;&#41;;)

[//]: # (     Tìm item sớm nhất và push notification)

[//]: # (    )
[//]: # (    if &#40;savedInstanceState == null&#41; {)

[//]: # (        NavigationManager.showHome&#40;getSupportFragmentManager&#40;&#41;&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    // Setup bottom navigation listener)

[//]: # (    bottomNav.setOnItemSelectedListener&#40;item -> {)

[//]: # (        if &#40;id == R.id.nav_home&#41; {)

[//]: # (            NavigationManager.showHome&#40;getSupportFragmentManager&#40;&#41;&#41;;)

[//]: # (        } else if &#40;id == R.id.nav_map&#41; {)

[//]: # (            NavigationManager.showMap&#40;getSupportFragmentManager&#40;&#41;&#41;;)

[//]: # (        })

[//]: # (        // ...)

[//]: # (    }&#41;;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**2. AuthRequiredActivity &#40;Base class&#41;:**)

[//]: # (```java)

[//]: # (public abstract class AuthRequiredActivity extends BaseActivity {)

[//]: # (    @Override)

[//]: # (    protected void onCreate&#40;Bundle savedInstanceState&#41; {)

[//]: # (        super.onCreate&#40;savedInstanceState&#41;;)

[//]: # (        authRepository = new AuthRepositoryImpl&#40;new FirebaseAuthService&#40;&#41;&#41;;)

[//]: # (        if &#40;!authRepository.isLoggedIn&#40;&#41;&#41; {)

[//]: # (            NavigationManager.goToLogin&#40;this&#41;; // Chuyển về Login nếu chưa đăng nhập)

[//]: # (            finish&#40;&#41;;)

[//]: # (        })

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**3. requestNecessaryPermissions&#40;&#41;:**)

[//]: # (```java)

[//]: # (private void requestNecessaryPermissions&#40;&#41; {)

[//]: # (    String[] permissions = {)

[//]: # (        Manifest.permission.ACCESS_FINE_LOCATION,)

[//]: # (        Manifest.permission.ACCESS_COARSE_LOCATION,)

[//]: # (        Manifest.permission.POST_NOTIFICATIONS,)

[//]: # (        Manifest.permission.USE_EXACT_ALARM)

[//]: # (    };)

[//]: # (    // Request permissions nếu chưa có)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (### 4. MÀN HÌNH HOME &#40;HomeFragment&#41;)

[//]: # ()
[//]: # (#### **HomeFragment.java** &#40;Hiển thị danh sách sản phẩm&#41;)

[//]: # ()
[//]: # (**Luồng hoạt động:**)

[//]: # ()
[//]: # (```)

[//]: # (HomeFragment.onCreateView&#40;&#41;)

[//]: # (    ↓)

[//]: # (Inflate layout &#40;fragment_home.xml&#41;)

[//]: # (    ↓)

[//]: # (Setup Hero Carousel &#40;ViewPager2&#41;)

[//]: # (    ↓)

[//]: # (Setup RecyclerView với ProductAdapter)

[//]: # (    ↓)

[//]: # (Load danh sách products)

[//]: # (    ↓)

[//]: # (User click product → ProductDetailsActivity)

[//]: # (```)

[//]: # ()
[//]: # (**Chi tiết code:**)

[//]: # ()
[//]: # (**1. onCreateView&#40;&#41;:**)

[//]: # (```java)

[//]: # (public View onCreateView&#40;LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState&#41; {)

[//]: # (    View view = inflater.inflate&#40;R.layout.fragment_home, container, false&#41;;)

[//]: # (    )
[//]: # (    // Setup Hero Carousel)

[//]: # (    ViewPager2 heroViewPager = view.findViewById&#40;R.id.heroViewPager&#41;;)

[//]: # (    HeroPagerAdapter heroAdapter = new HeroPagerAdapter&#40;requireContext&#40;&#41;, heroCards&#41;;)

[//]: # (    heroViewPager.setAdapter&#40;heroAdapter&#41;;)

[//]: # (    )
[//]: # (    // Auto-scroll hero carousel mỗi 4 giây)

[//]: # (    Handler heroHandler = new Handler&#40;&#41;;)

[//]: # (    Runnable heroAutoScroll = new Runnable&#40;&#41; {)

[//]: # (        @Override)

[//]: # (        public void run&#40;&#41; {)

[//]: # (            int next = &#40;currentHero[0] + 1&#41; % HERO_COUNT;)

[//]: # (            heroViewPager.setCurrentItem&#40;next, true&#41;;)

[//]: # (            heroHandler.postDelayed&#40;this, 4000&#41;;)

[//]: # (        })

[//]: # (    };)

[//]: # (    heroHandler.postDelayed&#40;heroAutoScroll, 4000&#41;;)

[//]: # (    )
[//]: # (    // Setup RecyclerView)

[//]: # (    recyclerView = view.findViewById&#40;R.id.recyclerViewProducts&#41;;)

[//]: # (    GridLayoutManager gridLayoutManager = new GridLayoutManager&#40;getContext&#40;&#41;, 2&#41;;)

[//]: # (    recyclerView.setLayoutManager&#40;gridLayoutManager&#41;;)

[//]: # (    )
[//]: # (    // Load products)

[//]: # (    productList = new ArrayList<>&#40;&#41;;)

[//]: # (    productList.add&#40;new Product&#40;"product_1", 40, 5, R.drawable.seat1&#41;&#41;;)

[//]: # (    // ...)

[//]: # (    )
[//]: # (    // Setup adapter với click listener)

[//]: # (    adapter = new ProductAdapter&#40;productList, &#40;product, position&#41; -> {)

[//]: # (        Intent intent = new Intent&#40;getContext&#40;&#41;, ProductDetailsActivity.class&#41;;)

[//]: # (        intent.putExtra&#40;"product_name", product.getName&#40;&#41;&#41;;)

[//]: # (        intent.putExtra&#40;"product_price", product.getPrice&#40;&#41;&#41;;)

[//]: # (        startActivity&#40;intent&#41;;)

[//]: # (    }&#41;;)

[//]: # (    recyclerView.setAdapter&#40;adapter&#41;;)

[//]: # (    )
[//]: # (    return view;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**2. Product Model:**)

[//]: # (```java)

[//]: # (public class Product {)

[//]: # (    private String name;      // Tên sản phẩm)

[//]: # (    private double price;    // Giá)

[//]: # (    private int number;       // Số lượng chỗ)

[//]: # (    private int imageResId;   // ID hình ảnh)

[//]: # (    )
[//]: # (    public Product&#40;String name, double price, int number, int imageResId&#41; {)

[//]: # (        this.name = name;)

[//]: # (        this.price = price;)

[//]: # (        this.number = number;)

[//]: # (        this.imageResId = imageResId;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (### 5. MÀN HÌNH CHI TIẾT SẢN PHẨM &#40;ProductDetailsActivity&#41;)

[//]: # ()
[//]: # (#### **ProductDetailsActivity.java** &#40;Chọn ngày, giờ, facilities và thêm vào giỏ&#41;)

[//]: # ()
[//]: # (**Luồng hoạt động:**)

[//]: # ()
[//]: # (```)

[//]: # (ProductDetailsActivity.onCreate&#40;&#41;)

[//]: # (    ↓)

[//]: # (Nhận data từ Intent &#40;product_name, product_price, ...&#41;)

[//]: # (    ↓)

[//]: # (Khởi tạo UI elements)

[//]: # (    ↓)

[//]: # (User chọn Date → showDatePickerDialog&#40;&#41;)

[//]: # (    ↓)

[//]: # (User chọn Slots → showSlotsSelectionDialog&#40;&#41;)

[//]: # (    ↓)

[//]: # (User chọn Facilities &#40;checkboxes&#41;)

[//]: # (    ↓)

[//]: # (calculatePrice&#40;&#41; → Cập nhật giá)

[//]: # (    ↓)

[//]: # (User click "Add to Cart")

[//]: # (    ↓)

[//]: # (ValidationUtils.isValidBookingTime&#40;&#41;)

[//]: # (    ↓)

[//]: # (Tạo CartItem → CartManager.addToCart&#40;&#41;)

[//]: # (    ↓)

[//]: # (finish&#40;&#41; → Quay về HomeFragment)

[//]: # (```)

[//]: # ()
[//]: # (**Chi tiết code:**)

[//]: # ()
[//]: # (**1. onCreate&#40;&#41;:**)

[//]: # (```java)

[//]: # (protected void onCreate&#40;Bundle savedInstanceState&#41; {)

[//]: # (    super.onCreate&#40;savedInstanceState&#41;;)

[//]: # (    setContentView&#40;R.layout.activity_product_details&#41;;)

[//]: # (    )
[//]: # (    // Lấy data từ Intent)

[//]: # (    Intent intent = getIntent&#40;&#41;;)

[//]: # (    String productName = intent.getStringExtra&#40;"product_name"&#41;;)

[//]: # (    double productPrice = intent.getDoubleExtra&#40;"product_price", 0.0&#41;;)

[//]: # (    int productNumber = intent.getIntExtra&#40;"product_number", 0&#41;;)

[//]: # (    )
[//]: # (    // Khởi tạo product)

[//]: # (    product = new Product&#40;productName, productPrice, productNumber, productImageResId&#41;;)

[//]: # (    )
[//]: # (    // Setup listeners)

[//]: # (    txtDate.setOnClickListener&#40;v -> showDatePickerDialog&#40;&#41;&#41;;)

[//]: # (    btnSelectSlots.setOnClickListener&#40;v -> showSlotsSelectionDialog&#40;&#41;&#41;;)

[//]: # (    btnAddToCart.setOnClickListener&#40;v -> addToCart&#40;&#41;&#41;;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**2. showDatePickerDialog&#40;&#41;:**)

[//]: # (```java)

[//]: # (private void showDatePickerDialog&#40;&#41; {)

[//]: # (    Calendar calendar = Calendar.getInstance&#40;&#41;;)

[//]: # (    DatePickerDialog datePickerDialog = new DatePickerDialog&#40;this,)

[//]: # (        &#40;view, selectedYear, selectedMonth, selectedDay&#41; -> {)

[//]: # (            Calendar selectedCalendar = Calendar.getInstance&#40;&#41;;)

[//]: # (            selectedCalendar.set&#40;selectedYear, selectedMonth, selectedDay&#41;;)

[//]: # (            selectedDate = selectedCalendar.getTime&#40;&#41;;)

[//]: # (            SimpleDateFormat sdf = new SimpleDateFormat&#40;"dd/MM/yyyy", Locale.getDefault&#40;&#41;&#41;;)

[//]: # (            txtDate.setText&#40;sdf.format&#40;selectedDate&#41;&#41;;)

[//]: # (            reloadPriceAndSlots&#40;&#41;; // Cập nhật giá và slots)

[//]: # (        }, year, month, day&#41;;)

[//]: # (    )
[//]: # (    // Không cho chọn ngày quá khứ)

[//]: # (    datePickerDialog.getDatePicker&#40;&#41;.setMinDate&#40;System.currentTimeMillis&#40;&#41; - 1000&#41;;)

[//]: # (    datePickerDialog.show&#40;&#41;;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**3. showSlotsSelectionDialog&#40;&#41;:**)

[//]: # (```java)

[//]: # (private void showSlotsSelectionDialog&#40;&#41; {)

[//]: # (    // Tạo danh sách slot names)

[//]: # (    CharSequence[] slotNames = new CharSequence[Slot.values&#40;&#41;.length];)

[//]: # (    for &#40;int i = 0; i < Slot.values&#40;&#41;.length; i++&#41; {)

[//]: # (        slotNames[i] = Slot.values&#40;&#41;[i].getFormattedTime&#40;&#41;; // "08:00 - 09:00")

[//]: # (    })

[//]: # (    )
[//]: # (    // Tạo checked items array)

[//]: # (    boolean[] checkedItems = new boolean[Slot.values&#40;&#41;.length];)

[//]: # (    for &#40;int i = 0; i < Slot.values&#40;&#41;.length; i++&#41; {)

[//]: # (        checkedItems[i] = selectedSlots.contains&#40;Slot.values&#40;&#41;[i]&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    AlertDialog.Builder builder = new AlertDialog.Builder&#40;this&#41;;)

[//]: # (    builder.setTitle&#40;"Choose time slots"&#41;;)

[//]: # (    builder.setMultiChoiceItems&#40;slotNames, checkedItems, &#40;dialog, which, isChecked&#41; -> {)

[//]: # (        checkedItems[which] = isChecked;)

[//]: # (    }&#41;;)

[//]: # (    builder.setPositiveButton&#40;"OK", &#40;dialog, which&#41; -> {)

[//]: # (        selectedSlots.clear&#40;&#41;;)

[//]: # (        for &#40;int i = 0; i < checkedItems.length; i++&#41; {)

[//]: # (            if &#40;checkedItems[i]&#41; {)

[//]: # (                selectedSlots.add&#40;Slot.values&#40;&#41;[i]&#41;;)

[//]: # (            })

[//]: # (        })

[//]: # (        reloadPriceAndSlots&#40;&#41;;)

[//]: # (    }&#41;;)

[//]: # (    builder.show&#40;&#41;;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**4. calculatePrice&#40;&#41;:**)

[//]: # (```java)

[//]: # (private double calculatePrice&#40;&#41; {)

[//]: # (    double totalPrice = product.getPrice&#40;&#41;; // Giá cơ bản)

[//]: # (    )
[//]: # (    // Nhân với số lượng slots)

[//]: # (    int slotCount = selectedSlots.size&#40;&#41;;)

[//]: # (    totalPrice *= slotCount;)

[//]: # (    )
[//]: # (    // Cộng giá facilities)

[//]: # (    if &#40;cbWhiteboard.isChecked&#40;&#41;&#41; totalPrice += Facility.WHITE_BOARD.getPrice&#40;&#41;;)

[//]: # (    if &#40;cbTV.isChecked&#40;&#41;&#41; totalPrice += Facility.TV.getPrice&#40;&#41;;)

[//]: # (    if &#40;cbMicrophone.isChecked&#40;&#41;&#41; totalPrice += Facility.MICROPHONE.getPrice&#40;&#41;;)

[//]: # (    if &#40;cbNetwork.isChecked&#40;&#41;&#41; totalPrice += Facility.NETWORK.getPrice&#40;&#41;;)

[//]: # (    )
[//]: # (    return totalPrice;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**5. addToCart&#40;&#41;:**)

[//]: # (```java)

[//]: # (btnAddToCart.setOnClickListener&#40;v -> {)

[//]: # (    // Validate)

[//]: # (    if &#40;!ValidationUtils.isValidBookingTime&#40;selectedDate, selectedSlots&#41;&#41; {)

[//]: # (        Toast.makeText&#40;this, "Invalid booking time", Toast.LENGTH_SHORT&#41;.show&#40;&#41;;)

[//]: # (        return;)

[//]: # (    })

[//]: # (    )
[//]: # (    if &#40;selectedDate == null || selectedSlots.isEmpty&#40;&#41;&#41; {)

[//]: # (        Toast.makeText&#40;this, "Please select date and slots", Toast.LENGTH_SHORT&#41;.show&#40;&#41;;)

[//]: # (        return;)

[//]: # (    })

[//]: # (    )
[//]: # (    // Lấy facilities đã chọn)

[//]: # (    selectedFacilities.clear&#40;&#41;;)

[//]: # (    if &#40;cbWhiteboard.isChecked&#40;&#41;&#41; selectedFacilities.add&#40;Facility.WHITE_BOARD&#41;;)

[//]: # (    // ...)

[//]: # (    )
[//]: # (    // Tạo CartItem)

[//]: # (    CartItem cartItem = new CartItem&#40;)

[//]: # (        product,)

[//]: # (        selectedFacilities,)

[//]: # (        selectedSlots,)

[//]: # (        selectedDate,)

[//]: # (        calculatePrice&#40;&#41;)

[//]: # (    &#41;;)

[//]: # (    )
[//]: # (    // Thêm vào cart)

[//]: # (    CartManager cartManager = CartManager.getInstance&#40;this&#41;;)

[//]: # (    cartManager.addToCart&#40;cartItem&#41;;)

[//]: # (    Toast.makeText&#40;this, "Added to cart", Toast.LENGTH_SHORT&#41;.show&#40;&#41;;)

[//]: # (    finish&#40;&#41;;)

[//]: # (}&#41;;)

[//]: # (```)

[//]: # ()
[//]: # (**6. Slot Enum:**)

[//]: # (```java)

[//]: # (public enum Slot {)

[//]: # (    SLOT_08_09&#40;LocalTime.of&#40;8, 0&#41;, LocalTime.of&#40;9, 0&#41;&#41;,)

[//]: # (    SLOT_09_10&#40;LocalTime.of&#40;9, 0&#41;, LocalTime.of&#40;10, 0&#41;&#41;,)

[//]: # (    // ...)

[//]: # (    )
[//]: # (    private final LocalTime start;)

[//]: # (    private final LocalTime end;)

[//]: # (    )
[//]: # (    public String getFormattedTime&#40;&#41; {)

[//]: # (        return start + " - " + end; // "08:00 - 09:00")

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**7. Facility Enum:**)

[//]: # (```java)

[//]: # (public enum Facility {)

[//]: # (    WHITE_BOARD&#40;"WHITE_BOARD", 10.0&#41;,)

[//]: # (    TV&#40;"TV", 15.0&#41;,)

[//]: # (    MICROPHONE&#40;"MICROPHONE", 8.0&#41;,)

[//]: # (    NETWORK&#40;"NETWORK", 5.0&#41;;)

[//]: # (    )
[//]: # (    private final String code;)

[//]: # (    private final double price;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (### 6. MÀN HÌNH GIỎ HÀNG &#40;CartFragment&#41;)

[//]: # ()
[//]: # (#### **CartFragment.java** &#40;Hiển thị và quản lý giỏ hàng&#41;)

[//]: # ()
[//]: # (**Luồng hoạt động:**)

[//]: # ()
[//]: # (```)

[//]: # (CartFragment.onCreateView&#40;&#41;)

[//]: # (    ↓)

[//]: # (CartManager.getInstance&#40;&#41; → Lấy cart items từ SharedPreferences)

[//]: # (    ↓)

[//]: # (Setup RecyclerView với CartAdapter)

[//]: # (    ↓)

[//]: # (updateSummary&#40;&#41; → Tính tổng tiền)

[//]: # (    ↓)

[//]: # (User click "Remove" → Xóa item → updateSummary&#40;&#41;)

[//]: # (    ↓)

[//]: # (User click "Checkout" → NavigationManager.showBilling&#40;&#41;)

[//]: # (```)

[//]: # ()
[//]: # (**Chi tiết code:**)

[//]: # ()
[//]: # (**1. onCreateView&#40;&#41;:**)

[//]: # (```java)

[//]: # (public View onCreateView&#40;LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState&#41; {)

[//]: # (    View view = inflater.inflate&#40;R.layout.fragment_cart, container, false&#41;;)

[//]: # (    )
[//]: # (    // Khởi tạo views)

[//]: # (    recyclerCart = view.findViewById&#40;R.id.recyclerCartItems&#41;;)

[//]: # (    txtSubtotal = view.findViewById&#40;R.id.txtSubtotal&#41;;)

[//]: # (    txtTax = view.findViewById&#40;R.id.txtTax&#41;;)

[//]: # (    txtTotal = view.findViewById&#40;R.id.txtTotal&#41;;)

[//]: # (    btnCheckout = view.findViewById&#40;R.id.btnCheckout&#41;;)

[//]: # (    )
[//]: # (    // Lấy cart items)

[//]: # (    cartManager = CartManager.getInstance&#40;requireContext&#40;&#41;&#41;;)

[//]: # (    cartList = cartManager.getCartItems&#40;&#41;;)

[//]: # (    )
[//]: # (    // Setup adapter với delete action)

[//]: # (    adapter = new CartAdapter&#40;cartList, position -> {)

[//]: # (        // Xóa item)

[//]: # (        CartExpiryScheduler.cancelExpiryAlarms&#40;requireContext&#40;&#41;, item, position&#41;;)

[//]: # (        cartManager.removeFromCart&#40;position&#41;;)

[//]: # (        cartList.clear&#40;&#41;;)

[//]: # (        cartList.addAll&#40;cartManager.getCartItems&#40;&#41;&#41;;)

[//]: # (        adapter.notifyItemRemoved&#40;position&#41;;)

[//]: # (        updateSummary&#40;&#41;;)

[//]: # (    }&#41;;)

[//]: # (    )
[//]: # (    recyclerCart.setLayoutManager&#40;new LinearLayoutManager&#40;getContext&#40;&#41;&#41;&#41;;)

[//]: # (    recyclerCart.setAdapter&#40;adapter&#41;;)

[//]: # (    )
[//]: # (    // Checkout button)

[//]: # (    btnCheckout.setOnClickListener&#40;v -> {)

[//]: # (        Bundle bundle = new Bundle&#40;&#41;;)

[//]: # (        Gson gson = new Gson&#40;&#41;;)

[//]: # (        String cartItemsJson = gson.toJson&#40;cartList&#41;;)

[//]: # (        bundle.putString&#40;"cartItemsJson", cartItemsJson&#41;;)

[//]: # (        NavigationManager.showBilling&#40;requireActivity&#40;&#41;.getSupportFragmentManager&#40;&#41;, bundle&#41;;)

[//]: # (    }&#41;;)

[//]: # (    )
[//]: # (    updateSummary&#40;&#41;;)

[//]: # (    return view;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**2. updateSummary&#40;&#41;:**)

[//]: # (```java)

[//]: # (private void updateSummary&#40;&#41; {)

[//]: # (    double subtotal = 0.0;)

[//]: # (    boolean hasExpired = false;)

[//]: # (    )
[//]: # (    for &#40;CartItem item : cartList&#41; {)

[//]: # (        subtotal += item.getPrice&#40;&#41;;)

[//]: # (        // Kiểm tra item có hết hạn không)

[//]: # (        if &#40;!ValidationUtils.isValidBookingTime&#40;item.getDate&#40;&#41;, item.getSlots&#40;&#41;&#41;&#41; {)

[//]: # (            item.setError&#40;"Expired"&#41;;)

[//]: # (            hasExpired = true;)

[//]: # (        } else {)

[//]: # (            item.setError&#40;""&#41;;)

[//]: # (        })

[//]: # (    })

[//]: # (    )
[//]: # (    double taxRate = 0.08; // 8% tax)

[//]: # (    double tax = subtotal * taxRate;)

[//]: # (    double total = subtotal + tax;)

[//]: # (    )
[//]: # (    txtSubtotal.setText&#40;String.format&#40;"$%.2f", subtotal&#41;&#41;;)

[//]: # (    txtTax.setText&#40;String.format&#40;"$%.2f", tax&#41;&#41;;)

[//]: # (    txtTotal.setText&#40;String.format&#40;"$%.2f", total&#41;&#41;;)

[//]: # (    )
[//]: # (    // Disable checkout nếu cart rỗng hoặc có item hết hạn)

[//]: # (    if &#40;cartList.isEmpty&#40;&#41; || hasExpired&#41; {)

[//]: # (        btnCheckout.setEnabled&#40;false&#41;;)

[//]: # (        btnCheckout.setAlpha&#40;0.5f&#41;;)

[//]: # (    } else {)

[//]: # (        btnCheckout.setEnabled&#40;true&#41;;)

[//]: # (        btnCheckout.setAlpha&#40;1.0f&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    adapter.notifyDataSetChanged&#40;&#41;;)

[//]: # (    )
[//]: # (    // Lên lịch notification cho các cart items)

[//]: # (    for &#40;int i = 0; i < cartList.size&#40;&#41;; i++&#41; {)

[//]: # (        CartExpiryScheduler.scheduleExpiryAlarms&#40;requireContext&#40;&#41;, cartList.get&#40;i&#41;, i&#41;;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**3. CartManager &#40;Singleton&#41;:**)

[//]: # (```java)

[//]: # (public class CartManager {)

[//]: # (    private static CartManager instance;)

[//]: # (    private final List<CartItem> cart;)

[//]: # (    private final SharedPreferences prefs;)

[//]: # (    private final Gson gson;)

[//]: # (    )
[//]: # (    private CartManager&#40;Context context&#41; {)

[//]: # (        cart = new ArrayList<>&#40;&#41;;)

[//]: # (        prefs = context.getSharedPreferences&#40;"CartPrefs", Context.MODE_PRIVATE&#41;;)

[//]: # (        gson = new Gson&#40;&#41;;)

[//]: # (        loadCartItems&#40;&#41;; // Load từ SharedPreferences)

[//]: # (    })

[//]: # (    )
[//]: # (    public static CartManager getInstance&#40;Context context&#41; {)

[//]: # (        if &#40;instance == null&#41; {)

[//]: # (            instance = new CartManager&#40;context.getApplicationContext&#40;&#41;&#41;;)

[//]: # (        })

[//]: # (        return instance;)

[//]: # (    })

[//]: # (    )
[//]: # (    public void addToCart&#40;CartItem cartItem&#41; {)

[//]: # (        cart.add&#40;cartItem&#41;;)

[//]: # (        saveCartItems&#40;&#41;; // Lưu vào SharedPreferences)

[//]: # (    })

[//]: # (    )
[//]: # (    private void saveCartItems&#40;&#41; {)

[//]: # (        SharedPreferences.Editor editor = prefs.edit&#40;&#41;;)

[//]: # (        String json = gson.toJson&#40;cart&#41;; // Convert sang JSON)

[//]: # (        editor.putString&#40;"cart_items", json&#41;;)

[//]: # (        editor.apply&#40;&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    private void loadCartItems&#40;&#41; {)

[//]: # (        String json = prefs.getString&#40;"cart_items", null&#41;;)

[//]: # (        if &#40;json != null&#41; {)

[//]: # (            Type type = new TypeToken<List<CartItem>>&#40;&#41;{}.getType&#40;&#41;;)

[//]: # (            List<CartItem> savedItems = gson.fromJson&#40;json, type&#41;;)

[//]: # (            cart.addAll&#40;savedItems&#41;;)

[//]: # (        })

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**4. CartItem Model:**)

[//]: # (```java)

[//]: # (public class CartItem {)

[//]: # (    private Date date;                    // Ngày đặt)

[//]: # (    private double price;                 // Tổng giá)

[//]: # (    private Product product;              // Sản phẩm)

[//]: # (    private List<Facility> _facilities;   // Facilities đã chọn)

[//]: # (    private List<Slot> slots;            // Slots đã chọn)

[//]: # (    private String error;                 // Lỗi &#40;nếu có&#41;)

[//]: # (    )
[//]: # (    public CartItem&#40;Product product, List<Facility> facilities, )

[//]: # (                   List<Slot> slots, Date date, double price&#41; {)

[//]: # (        this.product = product;)

[//]: # (        this._facilities = facilities;)

[//]: # (        this.slots = slots;)

[//]: # (        this.date = date;)

[//]: # (        this.price = price;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (### 7. MÀN HÌNH THANH TOÁN &#40;BillingFragment&#41;)

[//]: # ()
[//]: # (#### **BillingFragment.java** &#40;Nhập thông tin thanh toán và xác nhận booking&#41;)

[//]: # ()
[//]: # (**Luồng hoạt động:**)

[//]: # ()
[//]: # (```)

[//]: # (BillingFragment.onCreateView&#40;&#41;)

[//]: # (    ↓)

[//]: # (Nhận cartItemsJson từ Bundle)

[//]: # (    ↓)

[//]: # (Parse JSON → List<CartItem>)

[//]: # (    ↓)

[//]: # (Setup RecyclerView với BillingAdapter)

[//]: # (    ↓)

[//]: # (calculateTotalPrice&#40;&#41; → Hiển thị tổng tiền)

[//]: # (    ↓)

[//]: # (User nhập thông tin thẻ)

[//]: # (    ↓)

[//]: # (User click "Confirm Booking")

[//]: # (    ↓)

[//]: # (ValidationUtils.validateCardInfo&#40;&#41;)

[//]: # (    ↓)

[//]: # (SaveBookingUseCase.execute&#40;&#41; → Lưu vào SQLite)

[//]: # (    ↓)

[//]: # (CartManager.clearCart&#40;&#41; → Xóa giỏ hàng)

[//]: # (    ↓)

[//]: # (popBackStack&#40;&#41; → Quay về CartFragment)

[//]: # (```)

[//]: # ()
[//]: # (**Chi tiết code:**)

[//]: # ()
[//]: # (**1. onCreateView&#40;&#41;:**)

[//]: # (```java)

[//]: # (public View onCreateView&#40;LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState&#41; {)

[//]: # (    View view = inflater.inflate&#40;R.layout.fragment_billing, container, false&#41;;)

[//]: # (    )
[//]: # (    // Ẩn bottom navigation)

[//]: # (    &#40;&#40;MainActivity&#41; requireActivity&#40;&#41;&#41;.hideBottomNavigation&#40;&#41;;)

[//]: # (    )
[//]: # (    // Khởi tạo DatabaseHelper và UseCase)

[//]: # (    DatabaseHelper dbHelper = new DatabaseHelper&#40;requireContext&#40;&#41;&#41;;)

[//]: # (    saveBookingUseCase = new SaveBookingUseCase&#40;dbHelper&#41;;)

[//]: # (    )
[//]: # (    initViews&#40;view&#41;;)

[//]: # (    loadCartItems&#40;&#41;; // Load từ Bundle)

[//]: # (    setupRecyclerView&#40;&#41;;)

[//]: # (    calculateTotalPrice&#40;&#41;;)

[//]: # (    )
[//]: # (    btnConfirmBooking.setOnClickListener&#40;v -> confirmBooking&#40;&#41;&#41;;)

[//]: # (    return view;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**2. loadCartItems&#40;&#41;:**)

[//]: # (```java)

[//]: # (private void loadCartItems&#40;&#41; {)

[//]: # (    Bundle args = getArguments&#40;&#41;;)

[//]: # (    if &#40;args != null&#41; {)

[//]: # (        String cartItemsJson = args.getString&#40;"cartItemsJson"&#41;;)

[//]: # (        if &#40;cartItemsJson != null&#41; {)

[//]: # (            Gson gson = new Gson&#40;&#41;;)

[//]: # (            Type type = new TypeToken<List<CartItem>>&#40;&#41; {}.getType&#40;&#41;;)

[//]: # (            cartItems = gson.fromJson&#40;cartItemsJson, type&#41;; // Parse JSON)

[//]: # (        })

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**3. calculateTotalPrice&#40;&#41;:**)

[//]: # (```java)

[//]: # (private void calculateTotalPrice&#40;&#41; {)

[//]: # (    double subtotal = 0;)

[//]: # (    for &#40;CartItem item : cartItems&#41; {)

[//]: # (        subtotal += item.getPrice&#40;&#41;;)

[//]: # (    })

[//]: # (    double tax = subtotal * 0.08; // 8% tax)

[//]: # (    totalPrice = subtotal + tax;)

[//]: # (    )
[//]: # (    tvSubtotalPrice.setText&#40;String.format&#40;"$%.2f", subtotal&#41;&#41;;)

[//]: # (    tvTax.setText&#40;String.format&#40;"$%.2f", tax&#41;&#41;;)

[//]: # (    tvTotalPrice.setText&#40;String.format&#40;"$%.2f", totalPrice&#41;&#41;;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**4. confirmBooking&#40;&#41;:**)

[//]: # (```java)

[//]: # (private void confirmBooking&#40;&#41; {)

[//]: # (    String name = etCardholderName.getText&#40;&#41;.toString&#40;&#41;.trim&#40;&#41;;)

[//]: # (    String cardNumber = etCardNumber.getText&#40;&#41;.toString&#40;&#41;.trim&#40;&#41;;)

[//]: # (    String expiry = etExpiryDate.getText&#40;&#41;.toString&#40;&#41;.trim&#40;&#41;;)

[//]: # (    String cvv = etCvv.getText&#40;&#41;.toString&#40;&#41;.trim&#40;&#41;;)

[//]: # (    )
[//]: # (    // Validate)

[//]: # (    if &#40;!ValidationUtils.isValidCardholderName&#40;name&#41;&#41; {)

[//]: # (        etCardholderName.setError&#40;"Invalid name"&#41;;)

[//]: # (        return;)

[//]: # (    })

[//]: # (    if &#40;!ValidationUtils.isValidCardNumber&#40;cardNumber&#41;&#41; {)

[//]: # (        etCardNumber.setError&#40;"Invalid card number"&#41;;)

[//]: # (        return;)

[//]: # (    })

[//]: # (    // ... validate expiry, cvv)

[//]: # (    )
[//]: # (    // Lưu booking vào database)

[//]: # (    boolean success = saveBookingUseCase.execute&#40;cartItems, totalPrice&#41;;)

[//]: # (    if &#40;success&#41; {)

[//]: # (        CartManager cartManager = CartManager.getInstance&#40;requireContext&#40;&#41;&#41;;)

[//]: # (        cartManager.clearCart&#40;&#41;; // Xóa giỏ hàng)

[//]: # (        Toast.makeText&#40;requireContext&#40;&#41;, "Booking confirmed", Toast.LENGTH_SHORT&#41;.show&#40;&#41;;)

[//]: # (        getParentFragmentManager&#40;&#41;.popBackStack&#40;&#41;; // Quay về)

[//]: # (    } else {)

[//]: # (        Toast.makeText&#40;requireContext&#40;&#41;, "Booking failed", Toast.LENGTH_SHORT&#41;.show&#40;&#41;;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**5. SaveBookingUseCase:**)

[//]: # (```java)

[//]: # (public class SaveBookingUseCase {)

[//]: # (    private DatabaseHelper dbHelper;)

[//]: # (    )
[//]: # (    public SaveBookingUseCase&#40;DatabaseHelper dbHelper&#41; {)

[//]: # (        this.dbHelper = dbHelper;)

[//]: # (    })

[//]: # (    )
[//]: # (    public boolean execute&#40;List<CartItem> cartItems, double totalPrice&#41; {)

[//]: # (        // Lưu từng booking vào SQLite)

[//]: # (        for &#40;CartItem item : cartItems&#41; {)

[//]: # (            Booking booking = new Booking&#40;&#41;;)

[//]: # (            booking.setProductName&#40;item.getProduct&#40;&#41;.getName&#40;&#41;&#41;;)

[//]: # (            booking.setDate&#40;item.getDate&#40;&#41;&#41;;)

[//]: # (            booking.setPrice&#40;item.getPrice&#40;&#41;&#41;;)

[//]: # (            // ...)

[//]: # (            dbHelper.insertBooking&#40;booking&#41;;)

[//]: # (        })

[//]: # (        return true;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (### 8. MÀN HÌNH BẢN ĐỒ &#40;MapFragment&#41;)

[//]: # ()
[//]: # (#### **MapFragment.java** &#40;Hiển thị bản đồ Google Maps&#41;)

[//]: # ()
[//]: # (**Luồng hoạt động:**)

[//]: # ()
[//]: # (```)

[//]: # (MapFragment.onCreateView&#40;&#41;)

[//]: # (    ↓)

[//]: # (Inflate layout với MapView)

[//]: # (    ↓)

[//]: # (mapView.onCreate&#40;savedInstanceState&#41;)

[//]: # (    ↓)

[//]: # (mapView.getMapAsync&#40;&#41; → onMapReady&#40;&#41;)

[//]: # (    ↓)

[//]: # (MapUtils.setupLabMap&#40;&#41; → Đặt marker tại LAB location)

[//]: # (    ↓)

[//]: # (User click buttons → Recenter, Directions, Layers, etc.)

[//]: # (```)

[//]: # ()
[//]: # (**Chi tiết code:**)

[//]: # ()
[//]: # (**1. onCreateView&#40;&#41;:**)

[//]: # (```java)

[//]: # (public View onCreateView&#40;LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState&#41; {)

[//]: # (    View root = inflater.inflate&#40;R.layout.fragment_map, container, false&#41;;)

[//]: # (    )
[//]: # (    mapView = root.findViewById&#40;R.id.mapView&#41;;)

[//]: # (    mapView.onCreate&#40;savedInstanceState&#41;;)

[//]: # (    )
[//]: # (    // Setup buttons)

[//]: # (    btnRecenter = root.findViewById&#40;R.id.btnRecenter&#41;;)

[//]: # (    btnShowDirections = root.findViewById&#40;R.id.btnShowDirections&#41;;)

[//]: # (    btnMapLayers = root.findViewById&#40;R.id.btnMapLayers&#41;;)

[//]: # (    )
[//]: # (    // Setup map)

[//]: # (    mapView.getMapAsync&#40;googleMap -> {)

[//]: # (        // Enable current location)

[//]: # (        if &#40;hasLocationPermission&#40;&#41;&#41; {)

[//]: # (            googleMap.setMyLocationEnabled&#40;true&#41;;)

[//]: # (        })

[//]: # (    }&#41;;)

[//]: # (    )
[//]: # (    // Setup lab location)

[//]: # (    MapUtils.setupLabMap&#40;mapView, LAB_LAT, LAB_LON, LAB_NAME&#41;;)

[//]: # (    MapUtils.setupCompass&#40;btnCompass&#41;;)

[//]: # (    )
[//]: # (    // Button listeners)

[//]: # (    btnRecenter.setOnClickListener&#40;v -> recenterToUserLocation&#40;&#41;&#41;;)

[//]: # (    btnShowDirections.setOnClickListener&#40;v -> MapUtils.openDirections&#40;requireContext&#40;&#41;, LAB_NAME&#41;&#41;;)

[//]: # (    btnMapLayers.setOnClickListener&#40;v -> showMapLayersDialog&#40;&#41;&#41;;)

[//]: # (    )
[//]: # (    return root;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**2. recenterToUserLocation&#40;&#41;:**)

[//]: # (```java)

[//]: # (private void recenterToUserLocation&#40;&#41; {)

[//]: # (    if &#40;hasLocationPermission&#40;&#41;&#41; {)

[//]: # (        fusedLocationClient.getLastLocation&#40;&#41;.addOnSuccessListener&#40;location -> {)

[//]: # (            if &#40;location != null&#41; {)

[//]: # (                mapView.getMapAsync&#40;googleMap -> {)

[//]: # (                    LatLng userLatLng = new LatLng&#40;location.getLatitude&#40;&#41;, location.getLongitude&#40;&#41;&#41;;)

[//]: # (                    LatLng labLatLng = new LatLng&#40;LAB_LAT, LAB_LON&#41;;)

[//]: # (                    )
[//]: # (                    // Tạo bounds để hiển thị cả user và lab)

[//]: # (                    LatLngBounds.Builder builder = new LatLngBounds.Builder&#40;&#41;;)

[//]: # (                    builder.include&#40;userLatLng&#41;;)

[//]: # (                    builder.include&#40;labLatLng&#41;;)

[//]: # (                    LatLngBounds bounds = builder.build&#40;&#41;;)

[//]: # (                    )
[//]: # (                    googleMap.animateCamera&#40;CameraUpdateFactory.newLatLngBounds&#40;bounds, 120&#41;&#41;;)

[//]: # (                }&#41;;)

[//]: # (            })

[//]: # (        }&#41;;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**3. MapUtils.setupLabMap&#40;&#41;:**)

[//]: # (```java)

[//]: # (public static void setupLabMap&#40;MapView mapView, double lat, double lon, String name&#41; {)

[//]: # (    mapView.getMapAsync&#40;googleMap -> {)

[//]: # (        LatLng labLocation = new LatLng&#40;lat, lon&#41;;)

[//]: # (        googleMap.addMarker&#40;new MarkerOptions&#40;&#41;)

[//]: # (            .position&#40;labLocation&#41;)

[//]: # (            .title&#40;name&#41;&#41;;)

[//]: # (        googleMap.moveCamera&#40;CameraUpdateFactory.newLatLngZoom&#40;labLocation, 15f&#41;&#41;;)

[//]: # (    }&#41;;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (### 9. MÀN HÌNH CHAT &#40;ChatActivity&#41;)

[//]: # ()
[//]: # (#### **ChatActivity.java** &#40;Chat với AI chatbot hoặc support&#41;)

[//]: # ()
[//]: # (**Luồng hoạt động:**)

[//]: # ()
[//]: # (```)

[//]: # (ChatActivity.onCreate&#40;&#41;)

[//]: # (    ↓)

[//]: # (Khởi tạo MQTT service &#40;cho support chat&#41;)

[//]: # (    ↓)

[//]: # (Setup UI &#40;RecyclerView, EditText, buttons&#41;)

[//]: # (    ↓)

[//]: # (Load chat history từ Firebase)

[//]: # (    ↓)

[//]: # (User chọn tab &#40;Chatbot/Support&#41;)

[//]: # (    ↓)

[//]: # (User gửi message)

[//]: # (    ↓)

[//]: # (Chatbot: GeminiChatUtil.streamGeminiResponse&#40;&#41;)

[//]: # (    Support: mqttService.publish&#40;&#41;)

[//]: # (    ↓)

[//]: # (Nhận response → Hiển thị trong RecyclerView)

[//]: # (    ↓)

[//]: # (Lưu chat history vào Firebase)

[//]: # (```)

[//]: # ()
[//]: # (**Chi tiết code:**)

[//]: # ()
[//]: # (**1. onCreate&#40;&#41;:**)

[//]: # (```java)

[//]: # (protected void onCreate&#40;Bundle savedInstanceState&#41; {)

[//]: # (    super.onCreate&#40;savedInstanceState&#41;;)

[//]: # (    setContentView&#40;R.layout.activity_chat&#41;;)

[//]: # (    )
[//]: # (    // Khởi tạo MQTT service)

[//]: # (    mqttService = new MyMqttService&#40;&#41;;)

[//]: # (    new Thread&#40;&#40;&#41; -> mqttService.initialize&#40;&#41;&#41;.start&#40;&#41;;)

[//]: # (    )
[//]: # (    // Khởi tạo repository)

[//]: # (    chatRepository = new ChatRepositoryImpl&#40;new FirebaseChatServiceImpl&#40;&#41;&#41;;)

[//]: # (    authService = new FirebaseAuthService&#40;&#41;;)

[//]: # (    userId = authService.getCurrentUserId&#40;&#41;;)

[//]: # (    )
[//]: # (    // Setup UI)

[//]: # (    rvChat = findViewById&#40;R.id.rv_chat&#41;;)

[//]: # (    etMessage = findViewById&#40;R.id.et_message&#41;;)

[//]: # (    btnSend = findViewById&#40;R.id.btn_send&#41;;)

[//]: # (    )
[//]: # (    chatAdapter = new ChatAdapter&#40;chatMessages&#41;;)

[//]: # (    rvChat.setLayoutManager&#40;new LinearLayoutManager&#40;this&#41;&#41;;)

[//]: # (    rvChat.setAdapter&#40;chatAdapter&#41;;)

[//]: # (    )
[//]: # (    // Load chat history)

[//]: # (    loadChatHistory&#40;&#41;;)

[//]: # (    )
[//]: # (    // Button listeners)

[//]: # (    btnSend.setOnClickListener&#40;v -> sendMessage&#40;&#41;&#41;;)

[//]: # (    btnChatbot.setOnClickListener&#40;v -> switchToChatbot&#40;&#41;&#41;;)

[//]: # (    btnSupport.setOnClickListener&#40;v -> switchToSupport&#40;&#41;&#41;;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**2. sendMessage&#40;&#41;:**)

[//]: # (```java)

[//]: # (private void sendMessage&#40;&#41; {)

[//]: # (    String message = etMessage.getText&#40;&#41;.toString&#40;&#41;.trim&#40;&#41;;)

[//]: # (    if &#40;message.isEmpty&#40;&#41;&#41; return;)

[//]: # (    )
[//]: # (    if &#40;isChatbot&#41; {)

[//]: # (        // Chatbot mode - dùng Gemini AI)

[//]: # (        chatMessages.add&#40;new ChatMessage&#40;message, true&#41;&#41;; // User message)

[//]: # (        chatAdapter.notifyItemInserted&#40;chatMessages.size&#40;&#41; - 1&#41;;)

[//]: # (        )
[//]: # (        chatMessages.add&#40;new ChatMessage&#40;"...", false&#41;&#41;; // Loading)

[//]: # (        chatAdapter.notifyItemInserted&#40;chatMessages.size&#40;&#41; - 1&#41;;)

[//]: # (        )
[//]: # (        // Gọi Gemini API)

[//]: # (        GeminiChatUtil.streamGeminiResponse&#40;this, chatMessages, message, new GeminiChatUtil.GeminiStreamCallback&#40;&#41; {)

[//]: # (            @Override)

[//]: # (            public void onStreamUpdate&#40;String resp&#41; {)

[//]: # (                // Cập nhật response từng phần)

[//]: # (                chatMessages.set&#40;chatMessages.size&#40;&#41; - 1, new ChatMessage&#40;resp, false&#41;&#41;;)

[//]: # (                chatAdapter.notifyItemChanged&#40;chatMessages.size&#40;&#41; - 1&#41;;)

[//]: # (            })

[//]: # (            )
[//]: # (            @Override)

[//]: # (            public void onStreamComplete&#40;&#41; {)

[//]: # (                saveChatHistory&#40;&#41;;)

[//]: # (            })

[//]: # (        }&#41;;)

[//]: # (    } else {)

[//]: # (        // Support mode - dùng MQTT)

[//]: # (        ChatMessage chatMsg = new ChatMessage&#40;message, true&#41;;)

[//]: # (        supportMessages.add&#40;chatMsg&#41;;)

[//]: # (        chatMessages.add&#40;chatMsg&#41;;)

[//]: # (        chatAdapter.notifyItemInserted&#40;chatMessages.size&#40;&#41; - 1&#41;;)

[//]: # (        )
[//]: # (        // Publish message qua MQTT)

[//]: # (        try {)

[//]: # (            mqttService.publish&#40;supportRequestTopic, message&#41;;)

[//]: # (        } catch &#40;MqttException e&#41; {)

[//]: # (            e.printStackTrace&#40;&#41;;)

[//]: # (        })

[//]: # (        )
[//]: # (        saveSupportHistory&#40;&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    etMessage.setText&#40;""&#41;;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**3. loadChatHistory&#40;&#41;:**)

[//]: # (```java)

[//]: # (private void loadChatHistory&#40;&#41; {)

[//]: # (    if &#40;userId == null || userId.isEmpty&#40;&#41;&#41; return;)

[//]: # (    showLoading&#40;&#41;;)

[//]: # (    )
[//]: # (    chatRepository.loadChatHistory&#40;userId, new ChatRepository.ChatHistoryListener&#40;&#41; {)

[//]: # (        @Override)

[//]: # (        public void onHistoryLoaded&#40;List<ChatMessage> messages&#41; {)

[//]: # (            hideLoading&#40;&#41;;)

[//]: # (            chatMessages.clear&#40;&#41;;)

[//]: # (            chatMessages.addAll&#40;messages&#41;;)

[//]: # (            cachedChatbotMessages = new ArrayList<>&#40;messages&#41;;)

[//]: # (            chatAdapter.notifyDataSetChanged&#40;&#41;;)

[//]: # (            if &#40;!chatMessages.isEmpty&#40;&#41;&#41; {)

[//]: # (                rvChat.scrollToPosition&#40;chatMessages.size&#40;&#41; - 1&#41;;)

[//]: # (            })

[//]: # (        })

[//]: # (        )
[//]: # (        @Override)

[//]: # (        public void onError&#40;Exception e&#41; {)

[//]: # (            hideLoading&#40;&#41;;)

[//]: # (            Toast.makeText&#40;ChatActivity.this, "Error loading chat", Toast.LENGTH_SHORT&#41;.show&#40;&#41;;)

[//]: # (        })

[//]: # (    }&#41;;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**4. ChatRepositoryImpl:**)

[//]: # (```java)

[//]: # (public class ChatRepositoryImpl implements ChatRepository {)

[//]: # (    private FirebaseChatService chatService;)

[//]: # (    )
[//]: # (    @Override)

[//]: # (    public void loadChatHistory&#40;String userId, ChatHistoryListener listener&#41; {)

[//]: # (        chatService.loadChatHistory&#40;userId, listener&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    @Override)

[//]: # (    public void saveChatHistory&#40;String userId, List<ChatMessage> messages, SaveListener listener&#41; {)

[//]: # (        chatService.saveChatHistory&#40;userId, messages, listener&#41;;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**5. FirebaseChatServiceImpl:**)

[//]: # (```java)

[//]: # (public class FirebaseChatServiceImpl implements ChatService {)

[//]: # (    private FirebaseFirestore db = FirebaseFirestore.getInstance&#40;&#41;;)

[//]: # (    )
[//]: # (    @Override)

[//]: # (    public void loadChatHistory&#40;String userId, ChatHistoryListener listener&#41; {)

[//]: # (        db.collection&#40;"chats"&#41;)

[//]: # (            .document&#40;userId&#41;)

[//]: # (            .get&#40;&#41;)

[//]: # (            .addOnSuccessListener&#40;documentSnapshot -> {)

[//]: # (                List<ChatMessage> messages = documentSnapshot.toObject&#40;ChatHistory.class&#41;.getMessages&#40;&#41;;)

[//]: # (                listener.onHistoryLoaded&#40;messages&#41;;)

[//]: # (            }&#41;)

[//]: # (            .addOnFailureListener&#40;e -> listener.onError&#40;e&#41;&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    @Override)

[//]: # (    public void saveChatHistory&#40;String userId, List<ChatMessage> messages, SaveListener listener&#41; {)

[//]: # (        ChatHistory chatHistory = new ChatHistory&#40;messages&#41;;)

[//]: # (        db.collection&#40;"chats"&#41;)

[//]: # (            .document&#40;userId&#41;)

[//]: # (            .set&#40;chatHistory&#41;)

[//]: # (            .addOnSuccessListener&#40;aVoid -> listener.onSaved&#40;&#41;&#41;)

[//]: # (            .addOnFailureListener&#40;e -> listener.onError&#40;e&#41;&#41;;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (### 10. MÀN HÌNH CÀI ĐẶT &#40;SettingsFragment&#41;)

[//]: # ()
[//]: # (#### **SettingsFragment.java** &#40;Cài đặt app: theme, ngôn ngữ, logout, reset password&#41;)

[//]: # ()
[//]: # (**Luồng hoạt động:**)

[//]: # ()
[//]: # (```)

[//]: # (SettingsFragment.onCreateView&#40;&#41;)

[//]: # (    ↓)

[//]: # (Setup UI &#40;theme spinner, language spinner, buttons&#41;)

[//]: # (    ↓)

[//]: # (Load saved theme và language)

[//]: # (    ↓)

[//]: # (User chọn theme → ThemeUtils.saveTheme&#40;&#41; → recreate&#40;&#41;)

[//]: # (    ↓)

[//]: # (User chọn language → LocaleUtils.setLocale&#40;&#41; → recreate&#40;&#41;)

[//]: # (    ↓)

[//]: # (User click "Logout" → FirebaseAuthService.logout&#40;&#41; → goToLogin&#40;&#41;)

[//]: # (    ↓)

[//]: # (User click "Reset Password" → showResetPasswordDialog&#40;&#41;)

[//]: # (    ↓)

[//]: # (FirebaseAuthService.resetPassword&#40;&#41; → Gửi email reset)

[//]: # (```)

[//]: # ()
[//]: # (**Chi tiết code:**)

[//]: # ()
[//]: # (**1. onCreateView&#40;&#41;:**)

[//]: # (```java)

[//]: # (public View onCreateView&#40;LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState&#41; {)

[//]: # (    View view = inflater.inflate&#40;R.layout.fragment_settings, container, false&#41;;)

[//]: # (    )
[//]: # (    TextView logoutBtn = view.findViewById&#40;R.id.btn_login_logout&#41;;)

[//]: # (    TextView resetPasswordBtn = view.findViewById&#40;R.id.btn_reset_password&#41;;)

[//]: # (    TextView chatSupportBtn = view.findViewById&#40;R.id.btn_chat_support&#41;;)

[//]: # (    Spinner themeSpinner = view.findViewById&#40;R.id.spinner_theme&#41;;)

[//]: # (    Spinner languageSpinner = view.findViewById&#40;R.id.spinner_language&#41;;)

[//]: # (    )
[//]: # (    // Logout button)

[//]: # (    logoutBtn.setOnClickListener&#40;v -> {)

[//]: # (        new AlertDialog.Builder&#40;requireContext&#40;&#41;&#41;)

[//]: # (            .setTitle&#40;"Logout"&#41;)

[//]: # (            .setMessage&#40;"Are you sure?"&#41;)

[//]: # (            .setPositiveButton&#40;"OK", &#40;dialog, which&#41; -> {)

[//]: # (                authService.logout&#40;&#41;;)

[//]: # (                NavigationManager.goToLogin&#40;requireActivity&#40;&#41;&#41;;)

[//]: # (            }&#41;)

[//]: # (            .setNegativeButton&#40;"Cancel", null&#41;)

[//]: # (            .show&#40;&#41;;)

[//]: # (    }&#41;;)

[//]: # (    )
[//]: # (    // Reset password button)

[//]: # (    resetPasswordBtn.setOnClickListener&#40;v -> showResetPasswordDialog&#40;&#41;&#41;;)

[//]: # (    )
[//]: # (    // Chat support button)

[//]: # (    chatSupportBtn.setOnClickListener&#40;v -> NavigationManager.goToChat&#40;requireContext&#40;&#41;&#41;&#41;;)

[//]: # (    )
[//]: # (    // Theme spinner)

[//]: # (    int savedTheme = ThemeUtils.getSavedTheme&#40;requireContext&#40;&#41;&#41;;)

[//]: # (    themeSpinner.setSelection&#40;savedTheme&#41;;)

[//]: # (    themeSpinner.setOnItemSelectedListener&#40;new AdapterView.OnItemSelectedListener&#40;&#41; {)

[//]: # (        @Override)

[//]: # (        public void onItemSelected&#40;AdapterView<?> parent, View view, int position, long id&#41; {)

[//]: # (            if &#40;position != savedTheme&#41; {)

[//]: # (                ThemeUtils.saveTheme&#40;requireContext&#40;&#41;, position&#41;;)

[//]: # (                ThemeUtils.setTheme&#40;position&#41;;)

[//]: # (                requireActivity&#40;&#41;.recreate&#40;&#41;; // Reload activity với theme mới)

[//]: # (            })

[//]: # (        })

[//]: # (        @Override)

[//]: # (        public void onNothingSelected&#40;AdapterView<?> parent&#41; {})

[//]: # (    }&#41;;)

[//]: # (    )
[//]: # (    // Language spinner)

[//]: # (    String savedLang = LocaleUtils.getSavedLanguage&#40;requireContext&#40;&#41;&#41;;)

[//]: # (    int langPos = savedLang.equals&#40;"en"&#41; ? 1 : 0;)

[//]: # (    languageSpinner.setSelection&#40;langPos&#41;;)

[//]: # (    languageSpinner.setOnItemSelectedListener&#40;new AdapterView.OnItemSelectedListener&#40;&#41; {)

[//]: # (        @Override)

[//]: # (        public void onItemSelected&#40;AdapterView<?> parent, View v, int position, long id&#41; {)

[//]: # (            String langCode = position == 1 ? "en" : "vi";)

[//]: # (            if &#40;!LocaleUtils.getSavedLanguage&#40;requireContext&#40;&#41;&#41;.equals&#40;langCode&#41;&#41; {)

[//]: # (                LocaleUtils.setLocale&#40;requireContext&#40;&#41;, langCode&#41;;)

[//]: # (                requireActivity&#40;&#41;.recreate&#40;&#41;; // Reload activity với ngôn ngữ mới)

[//]: # (            })

[//]: # (        })

[//]: # (        @Override)

[//]: # (        public void onNothingSelected&#40;AdapterView<?> parent&#41; {})

[//]: # (    }&#41;;)

[//]: # (    )
[//]: # (    return view;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**2. showResetPasswordDialog&#40;&#41;:**)

[//]: # (```java)

[//]: # (private void showResetPasswordDialog&#40;&#41; {)

[//]: # (    EditText input = new EditText&#40;getContext&#40;&#41;&#41;;)

[//]: # (    input.setInputType&#40;InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS&#41;;)

[//]: # (    input.setHint&#40;"Email"&#41;;)

[//]: # (    )
[//]: # (    new AlertDialog.Builder&#40;getContext&#40;&#41;&#41;)

[//]: # (        .setTitle&#40;"Reset Password"&#41;)

[//]: # (        .setMessage&#40;"Enter your email to receive reset link"&#41;)

[//]: # (        .setView&#40;input&#41;)

[//]: # (        .setPositiveButton&#40;"OK", &#40;dialog, which&#41; -> {)

[//]: # (            String email = input.getText&#40;&#41;.toString&#40;&#41;.trim&#40;&#41;;)

[//]: # (            if &#40;!email.isEmpty&#40;&#41;&#41; {)

[//]: # (                showLoading&#40;&#41;;)

[//]: # (                authService.resetPassword&#40;email&#41;)

[//]: # (                    .addOnSuccessListener&#40;aVoid -> {)

[//]: # (                        hideLoading&#40;&#41;;)

[//]: # (                        Toast.makeText&#40;getContext&#40;&#41;, "Reset email sent", Toast.LENGTH_LONG&#41;.show&#40;&#41;;)

[//]: # (                    }&#41;)

[//]: # (                    .addOnFailureListener&#40;e -> {)

[//]: # (                        hideLoading&#40;&#41;;)

[//]: # (                        Toast.makeText&#40;getContext&#40;&#41;, "Error", Toast.LENGTH_SHORT&#41;.show&#40;&#41;;)

[//]: # (                    }&#41;;)

[//]: # (            })

[//]: # (        }&#41;)

[//]: # (        .setNegativeButton&#40;"Cancel", null&#41;)

[//]: # (        .show&#40;&#41;;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (### 11. DATABASE HELPER &#40;SQLite&#41;)

[//]: # ()
[//]: # (#### **DatabaseHelper.java** &#40;Quản lý SQLite database&#41;)

[//]: # ()
[//]: # (**Chức năng:**)

[//]: # (- Lưu trữ booking history)

[//]: # (- Quản lý cart items &#40;deprecated, hiện dùng SharedPreferences&#41;)

[//]: # (- SQLite operations: insert, query, delete)

[//]: # ()
[//]: # (**Chi tiết code:**)

[//]: # ()
[//]: # (**1. Database Schema:**)

[//]: # (```java)

[//]: # (public class DatabaseHelper extends SQLiteOpenHelper {)

[//]: # (    private static final String DATABASE_NAME = "lab_booking.db";)

[//]: # (    private static final int DATABASE_VERSION = 1;)

[//]: # (    )
[//]: # (    private static final String TABLE_CART = "cart";)

[//]: # (    private static final String TABLE_BOOKINGS = "bookings";)

[//]: # (    )
[//]: # (    @Override)

[//]: # (    public void onCreate&#40;SQLiteDatabase db&#41; {)

[//]: # (        // Tạo bảng cart)

[//]: # (        String createCartTable = "CREATE TABLE " + TABLE_CART + " &#40;" +)

[//]: # (            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +)

[//]: # (            "package_name TEXT, " +)

[//]: # (            "details TEXT, " +)

[//]: # (            "price REAL&#41;";)

[//]: # (        )
[//]: # (        // Tạo bảng bookings)

[//]: # (        String createBookingsTable = "CREATE TABLE " + TABLE_BOOKINGS + " &#40;" +)

[//]: # (            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +)

[//]: # (            "package_name TEXT, " +)

[//]: # (            "details TEXT, " +)

[//]: # (            "price REAL, " +)

[//]: # (            "timestamp TEXT&#41;";)

[//]: # (        )
[//]: # (        db.execSQL&#40;createCartTable&#41;;)

[//]: # (        db.execSQL&#40;createBookingsTable&#41;;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**2. saveBooking&#40;&#41;:**)

[//]: # (```java)

[//]: # (public boolean saveBooking&#40;List<CartItem> items, double totalPrice&#41; {)

[//]: # (    SQLiteDatabase db = getWritableDatabase&#40;&#41;;)

[//]: # (    db.beginTransaction&#40;&#41;; // Bắt đầu transaction)

[//]: # (    try {)

[//]: # (        for &#40;CartItem item : items&#41; {)

[//]: # (            ContentValues values = new ContentValues&#40;&#41;;)

[//]: # (            values.put&#40;"package_name", item.getProduct&#40;&#41;.getName&#40;&#41;&#41;;)

[//]: # (            values.put&#40;"price", item.getPrice&#40;&#41;&#41;;)

[//]: # (            values.put&#40;"timestamp", System.currentTimeMillis&#40;&#41; + ""&#41;;)

[//]: # (            db.insert&#40;TABLE_BOOKINGS, null, values&#41;; // Insert vào database)

[//]: # (        })

[//]: # (        db.setTransactionSuccessful&#40;&#41;; // Commit transaction)

[//]: # (        return true;)

[//]: # (    } catch &#40;Exception e&#41; {)

[//]: # (        return false; // Rollback nếu có lỗi)

[//]: # (    } finally {)

[//]: # (        db.endTransaction&#40;&#41;; // Kết thúc transaction)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**3. getCartItems&#40;&#41;:**)

[//]: # (```java)

[//]: # (public List<CartItem> getCartItems&#40;&#41; {)

[//]: # (    List<CartItem> items = new ArrayList<>&#40;&#41;;)

[//]: # (    SQLiteDatabase db = getReadableDatabase&#40;&#41;;)

[//]: # (    Cursor cursor = db.query&#40;TABLE_CART, null, null, null, null, null, null&#41;;)

[//]: # (    )
[//]: # (    while &#40;cursor.moveToNext&#40;&#41;&#41; {)

[//]: # (        CartItem item = new CartItem&#40;&#41;;)

[//]: # (        item.setPrice&#40;cursor.getDouble&#40;cursor.getColumnIndexOrThrow&#40;"price"&#41;&#41;&#41;;)

[//]: # (        items.add&#40;item&#41;;)

[//]: # (    })

[//]: # (    cursor.close&#40;&#41;;)

[//]: # (    return items;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (**Luồng lưu booking:**)

[//]: # (```)

[//]: # (BillingFragment.confirmBooking&#40;&#41;)

[//]: # (    ↓)

[//]: # (SaveBookingUseCase.execute&#40;&#41;)

[//]: # (    ↓)

[//]: # (DatabaseHelper.saveBooking&#40;&#41;)

[//]: # (    ↓)

[//]: # (SQLiteDatabase.beginTransaction&#40;&#41;)

[//]: # (    ↓)

[//]: # (db.insert&#40;&#41; cho từng CartItem)

[//]: # (    ↓)

[//]: # (db.setTransactionSuccessful&#40;&#41;)

[//]: # (    ↓)

[//]: # (db.endTransaction&#40;&#41;)

[//]: # (    ↓)

[//]: # (Booking được lưu vào SQLite)

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (### 12. NAVIGATION MANAGER)

[//]: # ()
[//]: # (#### **NavigationManager.java** &#40;Quản lý điều hướng&#41;)

[//]: # ()
[//]: # (**Chức năng:**)

[//]: # (- Quản lý tất cả navigation trong app)

[//]: # (- Chuyển đổi giữa các Activity và Fragment)

[//]: # ()
[//]: # (**Chi tiết code:**)

[//]: # ()
[//]: # (```java)

[//]: # (public class NavigationManager {)

[//]: # (    // Chuyển đến LoginActivity)

[//]: # (    public static void goToLogin&#40;Context context&#41; {)

[//]: # (        context.startActivity&#40;new Intent&#40;context, LoginActivity.class&#41;&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    // Chuyển đến MainActivity)

[//]: # (    public static void goToMain&#40;Context context&#41; {)

[//]: # (        context.startActivity&#40;new Intent&#40;context, MainActivity.class&#41;&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    // Hiển thị HomeFragment)

[//]: # (    public static void showHome&#40;FragmentManager fm&#41; {)

[//]: # (        showFragment&#40;fm, new HomeFragment&#40;&#41;&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    // Hiển thị MapFragment)

[//]: # (    public static void showMap&#40;FragmentManager fm&#41; {)

[//]: # (        showFragment&#40;fm, new MapFragment&#40;&#41;&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    // Hiển thị CartFragment)

[//]: # (    public static void showCart&#40;FragmentManager fm&#41; {)

[//]: # (        showFragment&#40;fm, new CartFragment&#40;&#41;&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    // Hiển thị BillingFragment với Bundle)

[//]: # (    public static void showBilling&#40;FragmentManager fm, Bundle args&#41; {)

[//]: # (        BillingFragment fragment = new BillingFragment&#40;&#41;;)

[//]: # (        if &#40;args != null&#41; fragment.setArguments&#40;args&#41;;)

[//]: # (        fm.beginTransaction&#40;&#41;)

[//]: # (            .setCustomAnimations&#40;R.anim.slide_in_right, R.anim.slide_out_left&#41;)

[//]: # (            .replace&#40;R.id.container, fragment&#41;)

[//]: # (            .addToBackStack&#40;null&#41;)

[//]: # (            .commit&#40;&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    // Helper method để show fragment)

[//]: # (    private static void showFragment&#40;FragmentManager fm, Fragment fragment&#41; {)

[//]: # (        fm.beginTransaction&#40;&#41;)

[//]: # (            .setCustomAnimations&#40;R.anim.slide_in_right, R.anim.slide_out_left&#41;)

[//]: # (            .replace&#40;R.id.container, fragment&#41;)

[//]: # (            .addToBackStack&#40;null&#41;)

[//]: # (            .commit&#40;&#41;;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (### 11. BASE CLASSES)

[//]: # ()
[//]: # (#### **BaseActivity.java** &#40;Base class cho tất cả Activity&#41;)

[//]: # ()
[//]: # (**Chức năng:**)

[//]: # (- Quản lý loading overlay)

[//]: # (- Quản lý network status bar)

[//]: # (- Xử lý network connectivity changes)

[//]: # ()
[//]: # (**Chi tiết code:**)

[//]: # ()
[//]: # (```java)

[//]: # (public abstract class BaseActivity extends AppCompatActivity {)

[//]: # (    private FrameLayout loadingOverlay;)

[//]: # (    private LinearLayout noInternetBar;)

[//]: # (    private BroadcastReceiver networkReceiver;)

[//]: # (    )
[//]: # (    protected void initLoadingOverlay&#40;&#41; {)

[//]: # (        loadingOverlay = findViewById&#40;R.id.loadingOverlay&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    public void showLoading&#40;&#41; {)

[//]: # (        if &#40;loadingOverlay != null&#41; loadingOverlay.setVisibility&#40;View.VISIBLE&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    public void hideLoading&#40;&#41; {)

[//]: # (        if &#40;loadingOverlay != null&#41; loadingOverlay.setVisibility&#40;View.GONE&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    @Override)

[//]: # (    protected void onResume&#40;&#41; {)

[//]: # (        super.onResume&#40;&#41;;)

[//]: # (        // Register network receiver)

[//]: # (        networkReceiver = new BroadcastReceiver&#40;&#41; {)

[//]: # (            @Override)

[//]: # (            public void onReceive&#40;Context context, Intent intent&#41; {)

[//]: # (                updateNoInternetBar&#40;&#41;;)

[//]: # (            })

[//]: # (        };)

[//]: # (        IntentFilter filter = new IntentFilter&#40;ConnectivityManager.CONNECTIVITY_ACTION&#41;;)

[//]: # (        registerReceiver&#40;networkReceiver, filter&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    private void updateNoInternetBar&#40;&#41; {)

[//]: # (        boolean isConnected = NetworkUtils.isNetworkAvailable&#40;this&#41;;)

[//]: # (        if &#40;!isConnected&#41; {)

[//]: # (            // Hiển thị bar màu vàng "No internet")

[//]: # (            noInternetBar.setVisibility&#40;View.VISIBLE&#41;;)

[//]: # (        } else {)

[//]: # (            // Hiển thị bar màu xanh "Internet back" trong 2 giây)

[//]: # (            noInternetBar.setVisibility&#40;View.VISIBLE&#41;;)

[//]: # (            handler.postDelayed&#40;&#40;&#41; -> noInternetBar.setVisibility&#40;View.GONE&#41;, 2000&#41;;)

[//]: # (        })

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (## 📊 SƠ ĐỒ LUỒNG DỮ LIỆU)

[//]: # ()
[//]: # (### Luồng Authentication:)

[//]: # (```)

[//]: # (LoginActivity)

[//]: # (    ↓)

[//]: # (AuthRepositoryImpl)

[//]: # (    ↓)

[//]: # (FirebaseAuthService)

[//]: # (    ↓)

[//]: # (Firebase Authentication API)

[//]: # (    ↓)

[//]: # (Callback → NavigationManager.goToMain&#40;&#41;)

[//]: # (```)

[//]: # ()
[//]: # (### Luồng Add to Cart:)

[//]: # (```)

[//]: # (ProductDetailsActivity)

[//]: # (    ↓)

[//]: # (User chọn date, slots, facilities)

[//]: # (    ↓)

[//]: # (calculatePrice&#40;&#41;)

[//]: # (    ↓)

[//]: # (Tạo CartItem)

[//]: # (    ↓)

[//]: # (CartManager.addToCart&#40;&#41;)

[//]: # (    ↓)

[//]: # (saveCartItems&#40;&#41; → SharedPreferences &#40;JSON&#41;)

[//]: # (```)

[//]: # ()
[//]: # (### Luồng Checkout:)

[//]: # (```)

[//]: # (CartFragment)

[//]: # (    ↓)

[//]: # (User click "Checkout")

[//]: # (    ↓)

[//]: # (BillingFragment &#40;với cartItemsJson&#41;)

[//]: # (    ↓)

[//]: # (User nhập card info)

[//]: # (    ↓)

[//]: # (SaveBookingUseCase.execute&#40;&#41;)

[//]: # (    ↓)

[//]: # (DatabaseHelper.insertBooking&#40;&#41;)

[//]: # (    ↓)

[//]: # (SQLite Database)

[//]: # (    ↓)

[//]: # (CartManager.clearCart&#40;&#41;)

[//]: # (```)

[//]: # ()
[//]: # (### Luồng Chat:)

[//]: # (```)

[//]: # (ChatActivity)

[//]: # (    ↓)

[//]: # (User gửi message)

[//]: # (    ↓)

[//]: # (ChatRepositoryImpl)

[//]: # (    ↓)

[//]: # (FirebaseChatServiceImpl &#40;hoặc GeminiChatUtil&#41;)

[//]: # (    ↓)

[//]: # (Firebase Firestore &#40;hoặc Gemini API&#41;)

[//]: # (    ↓)

[//]: # (Response → ChatAdapter → RecyclerView)

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (## 🔧 CÁC UTILITY CLASSES)

[//]: # ()
[//]: # (### **ValidationUtils.java** &#40;Các hàm validation&#41;)

[//]: # ()
[//]: # (**Chức năng:** Cung cấp các hàm kiểm tra tính hợp lệ của dữ liệu)

[//]: # ()
[//]: # (**Chi tiết các hàm:**)

[//]: # ()
[//]: # (**1. isValidEmail&#40;&#41;:**)

[//]: # (```java)

[//]: # (public static boolean isValidEmail&#40;String email&#41; {)

[//]: # (    return !TextUtils.isEmpty&#40;email&#41; && )

[//]: # (           Patterns.EMAIL_ADDRESS.matcher&#40;email&#41;.matches&#40;&#41;;)

[//]: # (})

[//]: # (```)

[//]: # (- Kiểm tra email không rỗng và đúng format &#40;có @, domain, etc.&#41;)

[//]: # ()
[//]: # (**2. isValidPassword&#40;&#41;:**)

[//]: # (```java)

[//]: # (public static boolean isValidPassword&#40;String password&#41; {)

[//]: # (    return !TextUtils.isEmpty&#40;password&#41; && password.length&#40;&#41; >= 6;)

[//]: # (})

[//]: # (```)

[//]: # (- Kiểm tra password có ít nhất 6 ký tự)

[//]: # ()
[//]: # (**3. isValidCardholderName&#40;&#41;:**)

[//]: # (```java)

[//]: # (public static boolean isValidCardholderName&#40;String name&#41; {)

[//]: # (    return name != null && )

[//]: # (           name.matches&#40;"^[a-zA-Z ]+$"&#41; && )

[//]: # (           !name.trim&#40;&#41;.isEmpty&#40;&#41;;)

[//]: # (})

[//]: # (```)

[//]: # (- Kiểm tra tên chỉ chứa chữ cái và khoảng trắng)

[//]: # ()
[//]: # (**4. isValidCardNumber&#40;&#41;:**)

[//]: # (```java)

[//]: # (public static boolean isValidCardNumber&#40;String number&#41; {)

[//]: # (    return number != null && number.matches&#40;"^\\d{16}$"&#41;;)

[//]: # (})

[//]: # (```)

[//]: # (- Kiểm tra số thẻ có đúng 16 chữ số)

[//]: # ()
[//]: # (**5. isValidExpiryDate&#40;&#41;:**)

[//]: # (```java)

[//]: # (public static boolean isValidExpiryDate&#40;String expiry&#41; {)

[//]: # (    if &#40;expiry == null || !expiry.matches&#40;"^\\d{2}/\\d{2}$"&#41;&#41; {)

[//]: # (        return false;)

[//]: # (    })

[//]: # (    SimpleDateFormat sdf = new SimpleDateFormat&#40;"MM/yy"&#41;;)

[//]: # (    Date expiryDate = sdf.parse&#40;expiry&#41;;)

[//]: # (    Date currentDate = new Date&#40;&#41;;)

[//]: # (    return expiryDate.after&#40;currentDate&#41;; // Phải sau ngày hiện tại)

[//]: # (})

[//]: # (```)

[//]: # (- Kiểm tra format MM/yy và phải sau ngày hiện tại)

[//]: # ()
[//]: # (**6. isValidCvv&#40;&#41;:**)

[//]: # (```java)

[//]: # (public static boolean isValidCvv&#40;String cvv&#41; {)

[//]: # (    return cvv != null && cvv.matches&#40;"^\\d{3}$"&#41;;)

[//]: # (})

[//]: # (```)

[//]: # (- Kiểm tra CVV có đúng 3 chữ số)

[//]: # ()
[//]: # (**7. isValidBookingTime&#40;&#41;:**)

[//]: # (```java)

[//]: # (public static boolean isValidBookingTime&#40;Date date, List<Slot> slots&#41; {)

[//]: # (    long remaining = getRemainingTimeUntilBooking&#40;date, slots&#41;;)

[//]: # (    return remaining > 0; // Còn thời gian mới hợp lệ)

[//]: # (})

[//]: # (```)

[//]: # (- Kiểm tra thời gian booking còn hợp lệ &#40;chưa quá khứ&#41;)

[//]: # ()
[//]: # (**8. getRemainingTimeUntilBooking&#40;&#41;:**)

[//]: # (```java)

[//]: # (public static long getRemainingTimeUntilBooking&#40;Date date, List<Slot> slots&#41; {)

[//]: # (    if &#40;date == null || slots == null || slots.isEmpty&#40;&#41;&#41; return -1;)

[//]: # (    )
[//]: # (    Slot firstSlot = slots.stream&#40;&#41;)

[//]: # (        .min&#40;&#40;a, b&#41; -> a.ordinal&#40;&#41; - b.ordinal&#40;&#41;&#41;)

[//]: # (        .orElse&#40;null&#41;;)

[//]: # (    )
[//]: # (    Calendar cal = Calendar.getInstance&#40;&#41;;)

[//]: # (    cal.setTime&#40;date&#41;;)

[//]: # (    cal.set&#40;Calendar.HOUR_OF_DAY, firstSlot.getStart&#40;&#41;.getHour&#40;&#41;&#41;;)

[//]: # (    cal.set&#40;Calendar.MINUTE, 0&#41;;)

[//]: # (    cal.set&#40;Calendar.SECOND, 0&#41;;)

[//]: # (    )
[//]: # (    long bookingTimeMillis = cal.getTimeInMillis&#40;&#41;;)

[//]: # (    long nowMillis = System.currentTimeMillis&#40;&#41;;)

[//]: # (    return bookingTimeMillis - nowMillis; // Trả về milliseconds còn lại)

[//]: # (})

[//]: # (```)

[//]: # (- Tính thời gian còn lại &#40;milliseconds&#41; từ bây giờ đến slot đầu tiên)

[//]: # ()
[//]: # (**9. getMergedSlotDisplayList&#40;&#41;:**)

[//]: # (```java)

[//]: # (public static List<String> getMergedSlotDisplayList&#40;List<Slot> slots&#41; {)

[//]: # (    // Sắp xếp slots theo thứ tự)

[//]: # (    List<Slot> sorted = new ArrayList<>&#40;slots&#41;;)

[//]: # (    sorted.sort&#40;&#40;a, b&#41; -> a.ordinal&#40;&#41; - b.ordinal&#40;&#41;&#41;;)

[//]: # (    )
[//]: # (    // Gộp các slots liên tiếp)

[//]: # (    // Ví dụ: [08-09, 09-10, 10-11] → "08:00 - 11:00")

[//]: # (    // [08-09, 11-12] → ["08:00 - 09:00", "11:00 - 12:00"])

[//]: # (    )
[//]: # (    int start = 0;)

[//]: # (    while &#40;start < sorted.size&#40;&#41;&#41; {)

[//]: # (        int end = start;)

[//]: # (        // Tìm các slots liên tiếp)

[//]: # (        while &#40;end + 1 < sorted.size&#40;&#41; && )

[//]: # (               sorted.get&#40;end + 1&#41;.ordinal&#40;&#41; == sorted.get&#40;end&#41;.ordinal&#40;&#41; + 1&#41; {)

[//]: # (            end++;)

[//]: # (        })

[//]: # (        )
[//]: # (        if &#40;start == end&#41; {)

[//]: # (            // Slot đơn lẻ)

[//]: # (            result.add&#40;sorted.get&#40;start&#41;.getFormattedTime&#40;&#41;&#41;;)

[//]: # (        } else {)

[//]: # (            // Nhiều slots liên tiếp → gộp lại)

[//]: # (            result.add&#40;sorted.get&#40;start&#41;.getStart&#40;&#41; + " - " + )

[//]: # (                       sorted.get&#40;end&#41;.getEnd&#40;&#41;&#41;;)

[//]: # (        })

[//]: # (        start = end + 1;)

[//]: # (    })

[//]: # (    return result;)

[//]: # (})

[//]: # (```)

[//]: # (- Gộp các slots liên tiếp thành chuỗi hiển thị &#40;ví dụ: "08:00 - 11:00"&#41;)

[//]: # ()
[//]: # (### **ThemeUtils.java**)

[//]: # (- `applyTheme&#40;&#41;`: Áp dụng theme &#40;dark/light mode&#41;)

[//]: # ()
[//]: # (### **LocaleUtils.java**)

[//]: # (- `applyLocale&#40;&#41;`: Áp dụng ngôn ngữ &#40;Vietnamese/English&#41;)

[//]: # ()
[//]: # (### **MapUtils.java**)

[//]: # (- `setupLabMap&#40;&#41;`: Setup map với marker)

[//]: # (- `openDirections&#40;&#41;`: Mở Google Maps directions)

[//]: # (- `changeMapType&#40;&#41;`: Đổi loại map &#40;normal/satellite/terrain&#41;)

[//]: # ()
[//]: # (### **NetworkUtils.java**)

[//]: # (- `isNetworkAvailable&#40;&#41;`: Kiểm tra có internet không)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (## 🗄️ DATABASE)

[//]: # ()
[//]: # (### **DatabaseHelper.java** &#40;SQLite&#41;)

[//]: # (- Lưu trữ booking history)

[//]: # (- Methods: `insertBooking&#40;&#41;`, `getAllBookings&#40;&#41;`, `deleteBooking&#40;&#41;`)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (## 📱 NOTIFICATIONS)

[//]: # ()
[//]: # (### **CartExpiryScheduler.java**)

[//]: # (- Lên lịch notification khi cart item sắp hết hạn)

[//]: # (- `scheduleExpiryAlarms&#40;&#41;`: Tạo alarm cho từng cart item)

[//]: # (- `pushImmediateNotification&#40;&#41;`: Push notification ngay lập tức)

[//]: # ()
[//]: # (### **CartExpiryReceiver.java**)

[//]: # (- BroadcastReceiver để nhận alarm và hiển thị notification)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (## 🎯 TÓM TẮT KIẾN TRÚC)

[//]: # ()
[//]: # (1. **Presentation Layer**:)

[//]: # (   - Activities: LoginActivity, MainActivity, ProductDetailsActivity, ChatActivity)

[//]: # (   - Fragments: HomeFragment, CartFragment, MapFragment, BillingFragment, SettingsFragment)

[//]: # (   - Adapters: ProductAdapter, CartAdapter, BillingAdapter, ChatAdapter)

[//]: # ()
[//]: # (2. **Domain Layer**:)

[//]: # (   - Models: Product, CartItem, Booking, Slot, Facility)

[//]: # (   - Repository Interfaces: AuthRepository, ChatRepository)

[//]: # (   - Use Cases: SaveBookingUseCase)

[//]: # ()
[//]: # (3. **Data Layer**:)

[//]: # (   - Repository Implementations: AuthRepositoryImpl, ChatRepositoryImpl)

[//]: # (   - Services: FirebaseAuthService, FirebaseChatServiceImpl)

[//]: # (   - Database: DatabaseHelper &#40;SQLite&#41;)

[//]: # (   - Storage: CartManager &#40;SharedPreferences&#41;)

[//]: # ()
[//]: # (4. **Utilities**:)

[//]: # (   - ValidationUtils, ThemeUtils, LocaleUtils, MapUtils, NetworkUtils)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (## 🔄 LUỒNG HOẠT ĐỘNG TỔNG QUAN)

[//]: # ()
[//]: # (```)

[//]: # (App Start)

[//]: # (    ↓)

[//]: # (LabBookingApp.onCreate&#40;&#41; → Firebase.initializeApp&#40;&#41;)

[//]: # (    ↓)

[//]: # (LoginActivity &#40;nếu chưa đăng nhập&#41;)

[//]: # (    ↓)

[//]: # (MainActivity &#40;nếu đã đăng nhập&#41;)

[//]: # (    ↓)

[//]: # (HomeFragment → Hiển thị products)

[//]: # (    ↓)

[//]: # (ProductDetailsActivity → Chọn date, slots, facilities)

[//]: # (    ↓)

[//]: # (CartFragment → Xem giỏ hàng)

[//]: # (    ↓)

[//]: # (BillingFragment → Thanh toán)

[//]: # (    ↓)

[//]: # (SaveBookingUseCase → Lưu vào SQLite)

[//]: # (    ↓)

[//]: # (CartManager.clearCart&#40;&#41; → Xóa giỏ hàng)

[//]: # (```)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (**Kết thúc tài liệu giải thích code!**)

[//]: # ()
