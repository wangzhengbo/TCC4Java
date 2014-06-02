int fib(int n)
{
    if (n <= 2)
        return 1;
    else
        return fib(n-1) + fib(n-2);
}

int foo(int n)
{
    printf("Hello World!\n");
    printf("fib(%d) = %d\n", n, fib(n));
    //printf("add(%d, %d) = %d\n", n, 2 * n, add(n, 2 * n));
    return 0;
}

int main(int argc, char** argv) {
	//printf("Hello, world, %d!\n", fib(10));
	/*printf("argc = %d\n", argc);
	int i = 0;
	for(i = 0; i < argc; i++) {
		printf("argv[%d] = %s\n", i, argv[i]);
	}
	return 3;*/
}